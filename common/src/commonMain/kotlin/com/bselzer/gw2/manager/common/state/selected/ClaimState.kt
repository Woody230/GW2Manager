package com.bselzer.gw2.manager.common.state.selected

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.ui.composable.ImageStateAdapter

data class ClaimState(
    val claimedAt: String,
    val claimedBy: String,
    override val link: String?,
    override val width: Int,
    override val height: Int,
    override val description: String,
    override val enabled: Boolean = true,
    override val color: Color? = null
) : ImageStateAdapter()