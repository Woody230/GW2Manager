package com.bselzer.gw2.manager.common.ui.layout.custom.objective.viewmodel

import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.common.Image
import com.bselzer.gw2.manager.common.ui.layout.custom.claim.viewmodel.ClaimViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.indicator.viewmodel.ObjectiveImageViewModel
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade

class ObjectiveOverviewViewModel(
    context: AppComponentContext,
    objective: WvwObjective,
    matchObjective: WvwMapObjective?,
    private val upgrade: WvwUpgrade?
) : ViewModel(context) {

    val image: Image = ObjectiveImageViewModel(
        context = context,
        objective = objective,
        matchObjective = matchObjective ?: WvwMapObjective()
    )

    val core: CoreViewModel? = matchObjective?.let { matchObjective ->
        CoreViewModel(
            context = this,
            matchObjective = matchObjective,
            upgrade = upgrade
        )
    }

    val match: CoreMatchViewModel = CoreMatchViewModel(
        context = this,
        objective = objective,
        matchObjective = matchObjective
    )

    val claim: ClaimViewModel = ClaimViewModel(
        context = this,
        objective = matchObjective
    )
}