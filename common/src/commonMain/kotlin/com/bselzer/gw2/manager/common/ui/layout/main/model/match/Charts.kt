package com.bselzer.gw2.manager.common.ui.layout.main.model.match

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc

data class Charts(
    val title: StringDesc,
    val color: Color,
    val charts: Collection<Chart>
)