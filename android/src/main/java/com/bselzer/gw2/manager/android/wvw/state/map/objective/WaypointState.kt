package com.bselzer.gw2.manager.android.wvw.state.map.objective

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.ui.composable.ImageState

data class WaypointState(
    override val enabled: Boolean,
    override val link: String?,
    override val description: String = "Waypoint",
    override val width: Int,
    override val height: Int,
    override val color: Color?
) : ImageState