package com.bselzer.gw2.manager.android.ui.activity.wvw.state.match.pie

data class ChartSliceState(
    val link: String,
    val width: Int,
    val height: Int,
    val description: String,

    /**
     * The starting angle in degrees.
     */
    val startAngle: Float,

    /**
     * The ending angle in degrees.
     */
    val endAngle: Float,
)