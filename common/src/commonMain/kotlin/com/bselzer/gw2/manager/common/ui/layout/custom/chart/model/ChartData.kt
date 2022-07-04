package com.bselzer.gw2.manager.common.ui.layout.custom.chart.model

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc

data class ChartData(
    val data: StringDesc,
    val color: Color,
    val owner: StringDesc
)