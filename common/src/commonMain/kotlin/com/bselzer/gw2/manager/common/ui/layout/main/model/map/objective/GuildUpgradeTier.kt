package com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective

import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.Flow

data class GuildUpgradeTier(
    val icon: Icon,
    val upgrades: Collection<Upgrade>,
    val description: Flow<StringDesc>,
)