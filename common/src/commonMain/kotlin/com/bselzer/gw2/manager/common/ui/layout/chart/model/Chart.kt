package com.bselzer.gw2.manager.common.ui.layout.chart.model

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

data class Chart(
    val title: StringDesc,
    val data: Collection<ChartData>,
    val background: ImageDesc,
    val divider: ImageDesc,
    val slices: Collection<ChartSlice>
)