package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.viewmodel

import com.bselzer.gw2.manager.common.configuration.wvw.WvwGuildUpgradeTier
import com.bselzer.gw2.manager.common.repository.data.generic.GuildData
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.Upgrade
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.UpgradeTier
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.UpgradeTierIcon
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveType
import com.bselzer.gw2.v2.model.enumeration.extension.decodeOrNull
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgradeId
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.ktx.logging.Logger

class GuildUpgradeTierViewModel(
    context: AppComponentContext,
    tier: WvwGuildUpgradeTier,
    objective: WvwMapObjective?
) : ViewModel(context), UpgradeTier, GuildData by context.repositories.guild {
    override val icon: UpgradeTierIcon = GuildUpgradeTierIconViewModel(
        context = this,
        tier = tier,
        startTime = objective?.claimedAt
    )

    private val type: WvwObjectiveType? = objective?.type?.decodeOrNull()
    private val typedUpgrades = tier.upgrades.filter { upgrade -> upgrade.availability.contains(type) }

    override val upgrades: Collection<Upgrade> = typedUpgrades.mapNotNull { upgrade ->
        val guildUpgradeId = GuildUpgradeId(upgrade.id)
        val guildUpgrade = guildUpgrades[guildUpgradeId]
        if (guildUpgrade == null) {
            Logger.w("Attempting to determine the state of a missing guild upgrade with id ${upgrade.id}")
            return@mapNotNull null
        }

        GuildUpgradeViewModel(
            context = context,
            upgrade = guildUpgrade,
            isUnlocked = objective?.guildUpgradeIds?.contains(guildUpgradeId) == true
        )
    }
}