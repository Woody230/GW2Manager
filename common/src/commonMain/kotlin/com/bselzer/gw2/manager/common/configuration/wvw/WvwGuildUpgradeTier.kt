package com.bselzer.gw2.manager.common.configuration.wvw

import kotlinx.serialization.Transient
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import kotlin.time.Duration

interface WvwGuildUpgradeTier {
    @XmlSerialName(value = "icon", namespace = "", prefix = "")
    val iconLink: String

    /**
     * The amount of time the objective must be held for this tier to be usable.
     */
    @XmlSerialName(value = "hold", namespace = "", prefix = "")
    val hold: String

    val upgrades: List<WvwGuildUpgrade>

    /**
     * The amount of time the objective must be held for this tier to be usable.
     */
    @Transient
    val holdingPeriod: Duration
        get() = Duration.parseOrNull(hold) ?: Duration.INFINITE
}