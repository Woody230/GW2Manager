package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.viewmodel

import com.bselzer.gw2.manager.common.configuration.wvw.WvwUpgradeProgression
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.UpgradeTier
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.UpgradeTiers
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.gw2.v2.model.wvw.upgrade.tier.WvwUpgradeTier
import com.bselzer.ktx.logging.Logger

class AutomaticUpgradeTiersViewModel(
    context: AppComponentContext,
    private val upgrade: WvwUpgrade,
    private val yaksDelivered: Int
) : ViewModel(context), UpgradeTiers {
    // Skip level 0 which only exists in the configuration.
    private val progressions = configuration.wvw.objectives.progressions.drop(1)

    override val tiers: Collection<UpgradeTier> = progressions.mapIndexedNotNull { index, progression ->
        tier(index)?.model(index, progression)
    }.filter { tier -> tier.upgrades.isNotEmpty() }

    private fun tier(index: Int): WvwUpgradeTier? {
        val tier = upgrade.tiers.getOrNull(index)
        if (tier == null) {
            Logger.w("Attempting to get a missing upgrade tier with index $index when there are ${upgrade.tiers.size} tiers.")
        }
        return tier
    }

    private fun WvwUpgradeTier.model(
        index: Int,
        progression: WvwUpgradeProgression
    ) = AutomaticUpgradeTierViewModel(
        context = this@AutomaticUpgradeTiersViewModel,
        index = index,
        progression = progression,
        tier = this,
        upgrade = upgrade,
        yaksDelivered = yaksDelivered
    )
}