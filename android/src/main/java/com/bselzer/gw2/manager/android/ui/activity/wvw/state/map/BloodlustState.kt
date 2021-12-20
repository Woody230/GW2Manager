package com.bselzer.gw2.manager.android.ui.activity.wvw.state.map

import androidx.compose.ui.graphics.Color

// TODO extend from image state
data class BloodlustState(
    val link: String,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val color: Color,
    val description: String
)