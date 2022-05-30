package com.bselzer.gw2.manager.common.repository.instance.generic

import androidx.compose.runtime.mutableStateMapOf
import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.instance.AppRepository
import com.bselzer.gw2.v2.model.guild.Guild
import com.bselzer.gw2.v2.model.guild.GuildId
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgradeId
import com.bselzer.ktx.kodein.db.operation.findByIds
import com.bselzer.ktx.kodein.db.operation.getById
import com.bselzer.ktx.kodein.db.operation.putMissingById
import com.bselzer.ktx.kodein.db.transaction.transaction

class GuildRepository(
    dependencies: RepositoryDependencies,
) : AppRepository(dependencies) {
    private val _guilds = mutableStateMapOf<GuildId, Guild>()
    val guilds: Map<GuildId, Guild> = _guilds

    private val _guildUpgrades = mutableStateMapOf<GuildUpgradeId, GuildUpgrade>()
    val guildUpgrades: Map<GuildUpgradeId, GuildUpgrade> = _guildUpgrades

    suspend fun updateGuild(guildId: GuildId) = database.transaction().use {
        val guild = getById(
            id = guildId,
            requestSingle = { clients.gw2.guild.guild(guildId) }
        )

        _guilds[guild.id] = guild
    }

    suspend fun updateGuildUpgrades(guildUpgradeIds: Collection<GuildUpgradeId>) = run {
        // MUST commit put before finding.
        database.transaction().use {
            // Note that some upgrades may not exist so the client defaulting these is preferred.
            putMissingById(
                requestIds = { guildUpgradeIds },
                requestById = { missingIds -> clients.gw2.guild.upgrades(missingIds) },
            )
        }

        database.transaction().use {
            val guildUpgrades: Collection<GuildUpgrade> = findByIds(guildUpgradeIds)
            guildUpgrades.forEach { guildUpgrade -> _guildUpgrades[guildUpgrade.id] = guildUpgrade }
        }
    }

    /**
     * Updates the guild upgrades declared in the configuration.
     *
     * This is required because there is no direct way to know what upgrades are associated with each tier.
     */
    suspend fun updateConfiguredGuildUpgrades() {
        val improvementIds = configuration.wvw.objectives.guildUpgrades.improvements.flatMap { improvement -> improvement.upgrades.map { upgrade -> upgrade.id } }
        val tacticIds = configuration.wvw.objectives.guildUpgrades.tactics.flatMap { tactic -> tactic.upgrades.map { upgrade -> upgrade.id } }
        val allIds = (improvementIds + tacticIds).map { id -> GuildUpgradeId(id) }
        updateGuildUpgrades(guildUpgradeIds = allIds)
    }
}