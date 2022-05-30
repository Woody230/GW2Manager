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
import com.bselzer.ktx.kodein.db.operation.findByIds
import com.bselzer.ktx.kodein.db.operation.putMissingById
import com.bselzer.ktx.kodein.db.transaction.transaction

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
        _match.value = match
        updateMapObjectives(match)
        updateMapGuildUpgrades(match)
    }

    private suspend fun updateMapObjectives(match: WvwMatch?) {
        val objectiveIds = match?.objectiveIds() ?: emptyList()

        // MUST commit put before finding.
        database.transaction().use {
            putMissingById(
                requestIds = { objectiveIds },
                requestById = { missingIds -> clients.gw2.wvw.objectives(missingIds) }
            )
        }

        database.transaction().use {
            val objectives: Collection<WvwObjective> = findByIds(objectiveIds)
            objectives.forEach { objective -> _objectives[objective.id] = objective }
            updateUpgrades(objectives)
        }
    }

    private suspend fun updateUpgrades(objectives: Collection<WvwObjective>) {
        val upgradeIds = objectives.map { objective -> objective.upgradeId }

        // MUST commit put before finding.
        database.transaction().use {
            putMissingById(
                // Note that some upgrades may not exist so the client defaulting these is preferred.
                requestIds = { upgradeIds },
                requestById = { missingIds -> clients.gw2.wvw.upgrades(missingIds) }
            )
        }

        database.transaction().use {
            val upgrades: Collection<WvwUpgrade> = findByIds(upgradeIds)
            upgrades.forEach { upgrade -> _upgrades[upgrade.id] = upgrade }
        }
    }

    private suspend fun updateMapGuildUpgrades(match: WvwMatch?) {
        val guildUpgradeIds = match?.guildUpgradeIds() ?: emptyList()
        repositories.guild.updateGuildUpgrades(guildUpgradeIds)
        repositories.guild.updateConfiguredGuildUpgrades()
    }
}