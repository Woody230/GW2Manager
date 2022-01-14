package com.bselzer.gw2.manager.common.state.match

import androidx.compose.ui.graphics.Color

data class ChartsState(
    val title: String,
    val color: Color,
    val charts: List<ChartState>
)