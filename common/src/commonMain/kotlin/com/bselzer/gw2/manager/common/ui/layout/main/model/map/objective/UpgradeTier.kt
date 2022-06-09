package com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective

data class UpgradeTier(
    val icon: TierDescriptor,
    val upgrades: Collection<Upgrade>
)