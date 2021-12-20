package com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.objective

import com.bselzer.gw2.manager.android.ui.activity.wvw.state.common.ImageState
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective

data class ObjectiveState(
    val objective: WvwObjective,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val progression: ProgressionState,
    val claim: ClaimState,
    val waypoint: WaypointState,
    val immunity: ImmunityState,
    val image: ImageState
)