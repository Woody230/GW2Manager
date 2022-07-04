package com.bselzer.gw2.manager.common.ui.layout.statistics.model.data

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc

data class Progress(
    val color: Color,
    val amount: Int,
    val percentage: Float,
    val owner: StringDesc
)