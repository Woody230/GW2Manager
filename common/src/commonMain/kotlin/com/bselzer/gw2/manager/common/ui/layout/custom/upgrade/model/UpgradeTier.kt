package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model

interface UpgradeTier {
    val icon: UpgradeTierIcon
    val upgrades: Collection<Upgrade>
}