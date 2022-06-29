package com.bselzer.gw2.manager.common.ui.layout.chart.model

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

data class ChartDataSet(
    val title: StringDesc,
    val icon: ImageDesc,
    val data: Collection<ChartData>,
)