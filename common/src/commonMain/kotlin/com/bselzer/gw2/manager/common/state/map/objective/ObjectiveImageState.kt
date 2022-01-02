package com.bselzer.gw2.manager.common.state.map.objective

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.ui.composable.ImageStateAdapter

data class ObjectiveImageState(
    override val link: String?,
    override val width: Int,
    override val height: Int,
    override val description: String?,
    override val color: Color,
) : ImageStateAdapter()