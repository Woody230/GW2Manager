package com.bselzer.gw2.manager.common.repository.instance.generic

import androidx.compose.runtime.mutableStateMapOf
import com.bselzer.gw2.manager.Guild
import com.bselzer.gw2.manager.GuildUpgrade
import com.bselzer.gw2.manager.common.database.query.getById
import com.bselzer.gw2.manager.common.database.query.putMissingById
import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.data.generic.GuildData
import com.bselzer.gw2.v2.intl.translation.Gw2Translators
import com.bselzer.gw2.v2.model.guild.GuildId
import com.bselzer.gw2.v2.model.guild.GuildLevel
import com.bselzer.gw2.v2.model.guild.upgrade.ClaimableUpgrade
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgradeId
import com.bselzer.ktx.function.collection.putInto
import com.bselzer.ktx.logging.Logger
import kotlin.time.Duration

class GuildRepository(
    dependencies: RepositoryDependencies,
    private val repositories: Repositories,
) : RepositoryDependencies by dependencies, GuildData {
    data class Repositories(
        val translation: TranslationRepository
    )

    private val _guilds = mutableStateMapOf<GuildId, Guild>()
    override val guilds: Map<GuildId, Guild> = _guilds

    private val _guildUpgrades = mutableStateMapOf<GuildUpgradeId, GuildUpgrade>()
    override val guildUpgrades: Map<GuildUpgradeId, GuildUpgrade> = _guildUpgrades

    suspend fun updateGuild(guildId: GuildId) = database.transaction {
        Logger.d { "Guild | Updating guild $guildId." }

        val guild = database.guildQueries.getById(
            id = guildId,
            requestSingle = { clients.gw2.guild.guild(guildId) }
        )

        _guilds[guild.Id] = guild
    }

    suspend fun updateGuildUpgrades(guildUpgradeIds: Collection<GuildUpgradeId>) = database.transaction {
        Logger.d { "Guild | Updating ${guildUpgradeIds.size} guild upgrades." }

        // Note that some upgrades may not exist so the client defaulting these is preferred.
        val guildUpgrades = database.guildUpgradeQueries.putMissingById(
            requestIds = { guildUpgradeIds },
            requestById = { missingIds -> clients.gw2.guild.upgrades(missingIds) },
        )

        guildUpgrades.putInto(_guildUpgrades)
        repositories.translation.updateTranslations(
            translator = Gw2Translators.guildUpgrade,
            defaults = guildUpgrades.values,
            requestTranslated = { missing, language -> clients.gw2.guild.upgrades(missing, language) }
        )
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