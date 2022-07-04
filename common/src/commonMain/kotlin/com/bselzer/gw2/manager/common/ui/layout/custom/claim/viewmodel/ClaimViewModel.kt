package com.bselzer.gw2.manager.common.ui.layout.custom.claim.viewmodel

import com.arkivanov.essenty.lifecycle.doOnResume
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.claim.model.Claim
import com.bselzer.gw2.v2.model.guild.Guild
import com.bselzer.gw2.v2.model.guild.GuildId
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.ktx.logging.Logger
import dev.icerock.moko.resources.format
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class ClaimViewModel(
    context: AppComponentContext,
    objective: WvwMapObjective?,
) : ViewModel(context) {
    private val guildId: GuildId? = objective?.claimedBy

    init {
        lifecycle.doOnResume {
            if (guildId != null && !guildId.isDefault) {
                scope.launch {
                    repositories.guild.updateGuild(guildId)
                }
            }
        }
    }

    private val claimedAt: Instant? = objective?.claimedAt

    private val guild: Guild?
        get() = repositories.guild.guilds[guildId]

    val exists: Boolean
        get() = claim != null

    val claim: Claim?
        get() {
            if (claimedAt == null || guildId == null) {
                return null
            }

            val guild = guild
            if (guild == null) {
                Logger.w("Attempting to find the claim for a missing guild with id $guildId")
                return null
            }

            return claim(at = claimedAt, guild = guild)
        }

    private fun claim(at: Instant, guild: Guild): Claim = Claim(
        claimedAt = configuration.wvw.claimedAt(at),
        claimedBy = AppResources.strings.claimed_by.format(guild.name.translated()),
        icon = ClaimImageViewModel(context = this, guild = guild)
    )
}