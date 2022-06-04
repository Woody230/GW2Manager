package com.bselzer.gw2.manager.common.repository.instance.specialized

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.instance.generic.GenericRepositories
import com.bselzer.gw2.v2.model.extension.wvw.guildUpgradeIds
import com.bselzer.gw2.v2.model.extension.wvw.objectiveIds
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgradeId
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.model.wvw.objective.WvwMapObjectiveId
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgradeId
import com.bselzer.ktx.function.collection.putInto
import com.bselzer.ktx.kodein.db.operation.putMissingById
import com.bselzer.ktx.kodein.db.transaction.transaction
import com.bselzer.ktx.logging.Logger

class WvwMatchRepository(
    dependencies: RepositoryDependencies,
    repositories: GenericRepositories
) : SpecializedRepository(dependencies, repositories), MatchData {
    private val _match = mutableStateOf<WvwMatch?>(null)
    override val match: WvwMatch?
        get() = _match.value

    private val _objectives = mutableStateMapOf<WvwMapObjectiveId, WvwObjective>()
    override val objectives: Map<WvwMapObjectiveId, WvwObjective> = _objectives

    private val _upgrades = mutableStateMapOf<WvwUpgradeId, WvwUpgrade>()
    override val upgrades: Map<WvwUpgradeId, WvwUpgrade> = _upgrades

    override val guildUpgrades: Map<GuildUpgradeId, GuildUpgrade> = repositories.guild.guildUpgrades

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
        val objectives = putMissingById(
            requestIds = { objectiveIds },
            requestById = { missingIds -> clients.gw2.wvw.objectives(missingIds) }
        )

        Logger.d { "Match | Updating ${objectives.size} objectives in match ${match?.id}." }
        objectives.putInto(_objectives)
        updateUpgrades(objectives.values)
    }

    private suspend fun updateUpgrades(objectives: Collection<WvwObjective>) = database.transaction().use {
        val upgradeIds = objectives.map { objective -> objective.upgradeId }
        Logger.d { "Match | Updating ${upgradeIds.size} upgrades for ${objectives.size} objectives." }

        putMissingById(
            // Note that some upgrades may not exist so the client defaulting these is preferred.
            requestIds = { upgradeIds },
            requestById = { missingIds -> clients.gw2.wvw.upgrades(missingIds) }
        ).putInto(_upgrades)
    }

    private suspend fun updateMapGuildUpgrades(match: WvwMatch?) {
        val guildUpgradeIds = match?.guildUpgradeIds() ?: emptyList()
        repositories.guild.updateGuildUpgrades(guildUpgradeIds)
        repositories.guild.updateConfiguredGuildUpgrades()
    }
}