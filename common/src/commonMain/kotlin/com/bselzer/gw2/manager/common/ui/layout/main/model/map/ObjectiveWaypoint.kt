package com.bselzer.gw2.manager.common.ui.layout.main.model.map

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

data class ObjectiveWaypoint(
    val enabled: Boolean,
    val link: ImageDesc?,
    val description: StringDesc,
    val width: Int,
    val height: Int,
    val color: Color?
)