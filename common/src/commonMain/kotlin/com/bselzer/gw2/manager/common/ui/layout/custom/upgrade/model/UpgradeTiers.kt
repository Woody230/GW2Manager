package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model

interface UpgradeTiers {
    val tiers: Collection<UpgradeTier>

    val shouldShowTiers: Boolean
        get() = tiers.any { tier -> tier.upgrades.isNotEmpty() }
}