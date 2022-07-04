package com.bselzer.gw2.manager.common.ui.layout.custom.chart.model

import dev.icerock.moko.resources.desc.StringDesc

data class ChartDataSet(
    val title: StringDesc,
    val data: Collection<ChartData>,
)