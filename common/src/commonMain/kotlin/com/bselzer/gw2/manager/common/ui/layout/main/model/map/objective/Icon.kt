package com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

data class Icon(
    val link: ImageDesc?,
    val width: Int,
    val height: Int,
    val description: StringDesc?,
    val color: Color?,
    val alpha: Float = DefaultAlpha
)