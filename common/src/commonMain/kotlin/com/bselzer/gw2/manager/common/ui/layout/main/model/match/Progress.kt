package com.bselzer.gw2.manager.common.ui.layout.main.model.match

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc

data class Progress(
    val color: Color,
    val amount: Int,
    val percentage: Float,
    val owner: StringDesc
)