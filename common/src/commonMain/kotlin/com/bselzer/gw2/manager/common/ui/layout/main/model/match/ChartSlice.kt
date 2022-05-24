package com.bselzer.gw2.manager.common.ui.layout.main.model.match

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

data class ChartSlice(
    val description: StringDesc,
    val startAngle: Float,
    val endAngle: Float,
    val image: ImageDesc
)