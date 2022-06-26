package com.bselzer.gw2.manager.common.configuration.wvw

import kotlin.time.Duration

interface WvwGuildUpgradeTier {
    val iconLink: String

    /**
     * The amount of time the objective must be held for this tier to be usable.
     */
    val hold: Duration

    val upgrades: List<WvwGuildUpgrade>
}