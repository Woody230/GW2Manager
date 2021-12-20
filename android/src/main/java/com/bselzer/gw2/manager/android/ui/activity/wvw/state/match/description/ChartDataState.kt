package com.bselzer.gw2.manager.android.ui.activity.wvw.state.match.description

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit

data class ChartDataState(
    val data: String,
    val color: Color,
    val textSize: TextUnit,
    val owner: String,
)