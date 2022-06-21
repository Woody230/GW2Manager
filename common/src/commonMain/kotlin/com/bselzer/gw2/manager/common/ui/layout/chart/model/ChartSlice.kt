package com.bselzer.gw2.manager.common.ui.layout.chart.model

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

data class ChartSlice(
    val description: StringDesc,
    val startAngle: Float,
    val endAngle: Float,
    val image: ImageDesc,
    val color: Color?
)