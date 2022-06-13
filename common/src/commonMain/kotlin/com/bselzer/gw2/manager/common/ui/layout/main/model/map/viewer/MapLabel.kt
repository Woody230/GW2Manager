package com.bselzer.gw2.manager.common.ui.layout.main.model.map.viewer

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.v2.tile.model.position.BoundedPosition
import dev.icerock.moko.resources.desc.StringDesc

data class MapLabel(
    val description: StringDesc,
    val color: Color,
    val position: BoundedPosition,
    val width: Double
)