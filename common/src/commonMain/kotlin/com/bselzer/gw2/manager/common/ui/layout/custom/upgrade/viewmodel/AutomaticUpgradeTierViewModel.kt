package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.viewmodel

import com.bselzer.gw2.manager.common.configuration.wvw.WvwUpgradeProgression
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.Upgrade
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.UpgradeTier
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.UpgradeTierIcon
import com.bselzer.gw2.v2.model.extension.wvw.tiers
import com.bselzer.gw2.v2.model.extension.wvw.yakRatios
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.gw2.v2.model.wvw.upgrade.tier.WvwUpgradeTier

class AutomaticUpgradeTierViewModel(
    context: AppComponentContext,
    index: Int,
    progression: WvwUpgradeProgression,
    tier: WvwUpgradeTier,
    upgrade: WvwUpgrade,
    yaksDelivered: Int
) : ViewModel(context), UpgradeTier {
    private val progressed = upgrade.tiers(yaksDelivered)
    private val yakRatios: List<Pair<Int, Int>> = upgrade.yakRatios(yaksDelivered).toList()
    private val yakRatio = yakRatios.getOrElse(index) { Pair(0, 0) }

    override val icon: UpgradeTierIcon = AutomaticUpgradeTierIconViewModel(
        context = context,
        progression = progression,
        tier = tier,
        yakRatio = yakRatio,
        isUnlocked = progressed.contains(tier)
    )

    override val upgrades: Collection<Upgrade> = tier.upgrades.map { upgrade ->
        AutomaticUpgradeViewModel(
            context = this,
            upgrade = upgrade,

            // Since these upgrades are automatic, if the tier is unlocked then the upgrade is unlocked.
            // Therefore the tier's alpha should be used.
            alpha = icon.alpha
        )
    }
}