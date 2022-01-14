package com.bselzer.gw2.manager.common.state.match.pie

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.ui.composable.ImageStateAdapter

data class ChartDividerState(
    override val link: String,
    override val width: Int,
    override val height: Int,
    override val enabled: Boolean = true,
    override val color: Color? = null,
    override val description: String? = null
) : ImageStateAdapter()