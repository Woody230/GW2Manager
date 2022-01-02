package com.bselzer.gw2.manager.common.state.selected.upgrade

import com.bselzer.gw2.manager.common.ui.composable.ImageStateAdapter

data class UpgradeTierState(
    override val link: String?,
    override val width: Int,
    override val height: Int,
    override val description: String,
    override val alpha: Float,
    val upgrades: Collection<UpgradeState>
) : ImageStateAdapter()