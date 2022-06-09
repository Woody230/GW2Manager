package com.bselzer.gw2.manager.common.ui.layout.main.model.map.viewer

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

data class Bloodlust(
    val link: ImageDesc,
    val x: Int,
    val y: Int,
    val color: Color,
    val description: StringDesc,
)