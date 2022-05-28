package com.bselzer.gw2.manager.common.repository.instance

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.base.AppRepository
import com.bselzer.gw2.v2.model.guild.GuildId
import com.bselzer.gw2.v2.model.guild.upgrade.DefaultUpgrade
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgradeId
import com.bselzer.ktx.kodein.db.operation.findByIds
import com.bselzer.ktx.kodein.db.operation.getById
import com.bselzer.ktx.kodein.db.operation.putMissingById
import com.bselzer.ktx.kodein.db.transaction.transaction

class GuildRepository(
    dependencies: RepositoryDependencies,
) : AppRepository(dependencies) {
    suspend fun getGuild(guildId: GuildId) = database.transaction().use {
        getById(
            id = guildId,
            requestSingle = { clients.gw2.guild.guild(guildId) }
        )
    }

    suspend fun getGuildUpgrades(guildUpgradeIds: Collection<GuildUpgradeId>): Collection<GuildUpgrade> = database.transaction().use {
        putMissingById(
            requestIds = { guildUpgradeIds },
            requestById = { missingIds -> clients.gw2.guild.upgrades(missingIds) },
            getId = { guildUpgrade -> guildUpgrade.id },

            // Need to default since some ids may not exist and this will prevent repeated API calls.
            default = { guildUpgradeId -> DefaultUpgrade(guildUpgradeId) }
        )

        return findByIds(guildUpgradeIds)
    }

    /**
     * Gets the guild upgrades declared in the configuration.
     *
     * This is required because there is no direct way to know what upgrades are associated with each tier.
     */
    suspend fun getConfiguredGuildUpgrades() = database.transaction().use {
        // Set up all the configured guild upgrades since
        val improvementIds = configuration.wvw.objectives.guildUpgrades.improvements.flatMap { improvement -> improvement.upgrades.map { upgrade -> upgrade.id } }
        val tacticIds = configuration.wvw.objectives.guildUpgrades.tactics.flatMap { tactic -> tactic.upgrades.map { upgrade -> upgrade.id } }
        val allIds = (improvementIds + tacticIds).map { id -> GuildUpgradeId(id) }
        getGuildUpgrades(guildUpgradeIds = allIds)
    }
}