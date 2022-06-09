package com.bselzer.gw2.manager.common.ui.layout.main.model.map.viewer

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

data class ObjectiveIcon(
    val objective: WvwObjective,
    val x: Int,
    val y: Int,
    val link: ImageDesc,
    val description: StringDesc,
    val color: Color,
    val progression: ObjectiveProgression,
    val claim: ObjectiveClaim,
    val waypoint: ObjectiveWaypoint,
    val immunity: ObjectiveImmunity,
)