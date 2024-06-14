package com.bselzer.gw2.manager.common.repository.instance.specialized

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.data.generic.GuildData
import com.bselzer.gw2.manager.common.repository.data.generic.OwnerData
import com.bselzer.gw2.manager.common.repository.data.generic.TranslateData
import com.bselzer.gw2.manager.common.repository.data.specialized.MatchData
import com.bselzer.gw2.manager.common.repository.instance.generic.GuildRepository
import com.bselzer.gw2.manager.common.repository.instance.generic.OwnerRepository
import com.bselzer.gw2.manager.common.repository.instance.generic.TranslationRepository
import com.bselzer.gw2.manager.common.repository.instance.generic.WorldRepository
import com.bselzer.gw2.v2.intl.translation.Gw2Translators
import com.bselzer.gw2.v2.model.enumeration.WvwMapType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.extension.decodeOrNull
import com.bselzer.gw2.v2.model.extension.wvw.*
import com.bselzer.gw2.v2.model.extension.wvw.count.WvwMatchObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.count.WvwSkirmishObjectiveOwnerCount
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.gw2.v2.model.wvw.map.WvwMap
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.model.wvw.objective.WvwMapObjectiveId
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgradeId
import com.bselzer.gw2.v2.resource.strings.stringDesc
import com.bselzer.ktx.db.operation.putMissingById
import com.bselzer.ktx.db.transaction.transaction
import com.bselzer.ktx.function.collection.putInto
import com.bselzer.ktx.logging.Logger
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class WvwMatchRepository(
    dependencies: RepositoryDependencies,
    private val repositories: Repositories
) : RepositoryDependencies by dependencies,
    MatchData,
    GuildData by repositories.guild,
    OwnerData by repositories.owner,
    TranslateData by repositories.translation {
    data class Repositories(
        val guild: GuildRepository,
        val owner: OwnerRepository,
        val translation: TranslationRepository,
        val world: WorldRepository
    )

    private val _match = mutableStateOf<WvwMatch?>(null)
    override val match: WvwMatch
        get() = _match.value ?: WvwMatch()

    @Suppress("UNCHECKED_CAST")
    override val maps: Map<WvwMapType, WvwMap>
        get() {
            val sorted = match.maps.sortedBy { map -> mapTypes.indexOf(map.type.decodeOrNull()) }
            val maps = sorted.associateBy { map -> map.type.decodeOrNull() }.filterKeys { type -> type != null && mapTypes.contains(type) }
            return maps as Map<WvwMapType, WvwMap>
        }

    override val count: WvwMatchObjectiveOwnerCount
        get() = match.objectiveOwnerCount()

    override val lastSkirmish: WvwSkirmishObjectiveOwnerCount
        get() = match.skirmishObjectiveOwnerCounts().maxByOrNull { (id, _) -> id }?.value ?: WvwSkirmishObjectiveOwnerCount()

    private val _objectives = mutableStateMapOf<WvwMapObjectiveId, WvwObjective>()
    override val objectives: Map<WvwMapObjectiveId, WvwObjective> = _objectives

    private val _upgrades = mutableStateMapOf<WvwUpgradeId, WvwUpgrade>()
    override val upgrades: Map<WvwUpgradeId, WvwUpgrade> = _upgrades

    /**
     * @return the displayable names for the linked worlds associated with the objective [owner]
     */
    override fun displayableLinkedWorlds(owner: WvwObjectiveOwner): StringDesc {
        val worlds = repositories.world.worlds
        val linkedIds = match.linkedWorlds(owner)
        val linkedWorlds: Collection<String> = linkedIds.mapNotNull { worldId -> worlds[worldId]?.name?.value }

        // Default to using the owner if there are no worlds.
        if (linkedWorlds.isEmpty()) {
            return owner.stringDesc()
        }

        // Make sure that the main world is first.
        val mainWorld: String? = match.mainWorld(owner)?.let { worldId -> worlds[worldId]?.name?.value }
        val sortedWorlds = if (mainWorld == null) {
            linkedWorlds
        } else {
            linkedWorlds.toMutableList().apply {
                remove(mainWorld)
                add(0, mainWorld)
            }
        }

        // Finally, translate and combine.
        return sortedWorlds.joinToString(
            separator = ", ",
            transform = { name -> name.translated() }
        ).desc()
    }

    /**
     * Updates the [match]'s [WvwObjective]s for each map and their associated [WvwUpgrade]s and claimable [GuildUpgrade]s.
     */
    suspend fun updateMatch(match: WvwMatch?) = database.transaction().use {
        Logger.d { "Match | Updating match ${match?.id}." }

        _match.value = match
        updateMapObjectives(match)
        updateMapGuildUpgrades(match)
    }

    private suspend fun updateMapObjectives(match: WvwMatch?) = database.transaction().use {
        val objectiveIds = match?.objectiveIds() ?: emptyList()
        Logger.d { "Match | Updating ${objectiveIds.size} objectives in match ${match?.id}." }

        val objectives = putMissingById(
            requestIds = { objectiveIds },
            requestById = { missingIds -> clients.gw2.wvw.objectives(missingIds) }
        )

        objectives.putInto(_objectives)
        repositories.translation.updateTranslations(
            translator = Gw2Translators.wvwObjective,
            defaults = objectives.values,
            requestTranslated = { missing, language -> clients.gw2.wvw.objectives(missing, language) }
        )

        updateUpgrades(objectives.values)
    }

    private suspend fun updateUpgrades(objectives: Collection<WvwObjective>) = database.transaction().use {
        val upgradeIds = objectives.map { objective -> objective.upgradeId }
        Logger.d { "Match | Updating ${upgradeIds.size} upgrades for ${objectives.size} objectives." }

        val upgrades = putMissingById(
            // Note that some upgrades may not exist so the client defaulting these is preferred.
            requestIds = { upgradeIds },
            requestById = { missingIds -> clients.gw2.wvw.upgrades(missingIds) }
        )

        upgrades.putInto(_upgrades)
        repositories.translation.updateTranslations(
            translator = Gw2Translators.wvwUpgrade,
            defaults = upgrades.values,
            requestTranslated = { missing, language -> clients.gw2.wvw.upgrades(missing, language) }
        )
    }

    private suspend fun updateMapGuildUpgrades(match: WvwMatch?) {
        val guildUpgradeIds = match?.guildUpgradeIds() ?: emptyList()
        repositories.guild.updateGuildUpgrades(guildUpgradeIds)
        repositories.guild.updateConfiguredGuildUpgrades()
    }
}