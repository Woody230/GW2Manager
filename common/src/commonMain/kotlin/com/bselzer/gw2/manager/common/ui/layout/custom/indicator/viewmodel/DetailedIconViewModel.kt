package com.bselzer.gw2.manager.common.ui.layout.custom.indicator.viewmodel

import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.v2.model.tile.position.BoundedPosition
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade

class DetailedIconViewModel(
    context: AppComponentContext,
    val objective: WvwObjective,
    matchObjective: WvwMapObjective,
    upgrade: WvwUpgrade?,
    val position: BoundedPosition,
) : ViewModel(context) {
    val id: String = "objective-${objective.id}"

    val image = ObjectiveImageViewModel(
        context = this,
        objective = objective,
        matchObjective = matchObjective
    )

    val progression = ProgressionIndicatorViewModel(
        context = this,
        matchObjective = matchObjective,
        upgrade = upgrade
    )

    val claim = ClaimIndicatorViewModel(
        context = this,
        matchObjective = matchObjective,
    )

    val waypoint = WaypointIndicatorViewModel(
        context = this,
        matchObjective = matchObjective,
        upgrade = upgrade
    )

    val immunity = ImmunityViewModel(
        context = this,
        matchObjective = matchObjective
    )
}