package com.bselzer.gw2.manager.common.state.match.pie

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.ui.composable.ImageStateAdapter

data class ChartSliceState(
    override val link: String,
    override val width: Int,
    override val height: Int,
    override val description: String,
    override val enabled: Boolean = true,
    override val color: Color? = null,

    /**
     * The starting angle in degrees.
     */
    val startAngle: Float,

    /**
     * The ending angle in degrees.
     */
    val endAngle: Float,
) : ImageStateAdapter()