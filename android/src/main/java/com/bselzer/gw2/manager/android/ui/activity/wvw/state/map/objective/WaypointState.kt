package com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.objective

import androidx.compose.ui.graphics.Color

data class WaypointState(
    override val enabled: Boolean,
    override val link: String?,
    override val description: String = "Waypoint",
    override val width: Int,
    override val height: Int,

    /**
     * The color to transform the image into.
     */
    val color: Color?
) : IndicatorState