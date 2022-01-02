package com.bselzer.gw2.manager.common.state.selected.guild

import androidx.compose.ui.graphics.DefaultAlpha
import com.bselzer.gw2.manager.common.ui.composable.ImageStateAdapter

data class GuildUpgradeState(
    override val link: String?,
    override val height: Int,
    override val width: Int,
    override val description: String,
    val name: String,
) : ImageStateAdapter() {
    override var alpha: Float = DefaultAlpha
}