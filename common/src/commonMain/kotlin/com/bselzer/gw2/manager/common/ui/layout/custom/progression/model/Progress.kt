package com.bselzer.gw2.manager.common.ui.layout.custom.progression.model

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc

data class Progress(
    val color: Color,
    val amount: Int,
    val percentage: Float,
    val owner: StringDesc
)