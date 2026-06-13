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
    private val matchObjective: WvwMapObjective,
    private val upgrade: WvwUpgrade?,
    val position: BoundedPosition,
) : ViewModel(context) {
    companion object {
        const val ID_PREFIX = "objective"
    }

    val id: String = "$ID_PREFIX-${objective.id}"

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        other as DetailedIconViewModel

        if (objective != other.objective) return false
        if (matchObjective != other.matchObjective) return false
        if (upgrade != other.upgrade) return false
        if (position != other.position) return false

        return true
    }

    override fun hashCode(): Int {
        var result = objective.hashCode()
        result = 31 * result + matchObjective.hashCode()
        result = 31 * result + (upgrade?.hashCode() ?: 0)
        result = 31 * result + position.hashCode()
        return result
    }
}