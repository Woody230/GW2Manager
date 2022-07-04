package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.viewmodel

import com.bselzer.gw2.manager.common.configuration.wvw.WvwGuildUpgradeTier
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.UpgradeTier
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.UpgradeTiers
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective

class GuildUpgradeTiersViewModel(
    context: AppComponentContext,
    tiers: List<WvwGuildUpgradeTier>,
    objective: WvwMapObjective?
) : ViewModel(context), UpgradeTiers {
    override val tiers: Collection<UpgradeTier> = tiers.map { tier ->
        GuildUpgradeTierViewModel(
            context = context,
            tier = tier,
            objective = objective
        )
    }.filter { tier -> tier.upgrades.isNotEmpty() }
}