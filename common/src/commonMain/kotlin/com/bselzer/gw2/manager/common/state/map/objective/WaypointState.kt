package com.bselzer.gw2.manager.common.state.map.objective

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.ui.composable.ImageStateAdapter

data class WaypointState(
    override val enabled: Boolean,
    override val link: String?,
    override val description: String = "Waypoint",
    override val width: Int,
    override val height: Int,
    override val color: Color?
) : ImageStateAdapter()