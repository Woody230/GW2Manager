package com.bselzer.gw2.manager.common.state.map

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.ui.composable.ImageStateAdapter

data class BloodlustState(
    override val link: String,
    val x: Int,
    val y: Int,
    override val width: Int,
    override val height: Int,
    override val color: Color,
    override val description: String,
    override val enabled: Boolean
) : ImageStateAdapter()