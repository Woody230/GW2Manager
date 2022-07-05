package com.bselzer.gw2.manager.common.ui.layout.custom.chart.model

import com.bselzer.gw2.manager.common.ui.layout.common.Image

data class Chart(
    val background: Image,
    val divider: Image,
    val slices: Collection<ChartSlice>
)