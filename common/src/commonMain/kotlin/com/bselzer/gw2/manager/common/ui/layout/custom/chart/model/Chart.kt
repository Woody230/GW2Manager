package com.bselzer.gw2.manager.common.ui.layout.custom.chart.model

import dev.icerock.moko.resources.desc.image.ImageDesc

data class Chart(
    val background: ImageDesc,
    val divider: ImageDesc,
    val slices: Collection<ChartSlice>
)