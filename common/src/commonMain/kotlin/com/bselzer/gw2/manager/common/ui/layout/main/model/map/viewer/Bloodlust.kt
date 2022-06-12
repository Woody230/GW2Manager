package com.bselzer.gw2.manager.common.ui.layout.main.model.map.viewer

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.v2.tile.model.position.BoundedPosition
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

data class Bloodlust(
    val link: ImageDesc,
    val position: BoundedPosition,
    val color: Color,
    val description: StringDesc,
)