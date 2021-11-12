package com.bselzer.gw2.manager.configuration.wvw

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwGuildUpgradeWaypoint(
    @XmlSerialName(value = "enabled", namespace = "", prefix = "")
    val enabled: Boolean = false,

    @XmlSerialName(value = "upgrade", namespace = "", prefix = "")
    val upgradeName: String = "^Emergency Waypoint$",

    /**
     * The color transformation as hex.
     */
    @XmlSerialName(value = "color", namespace = "", prefix = "")
    val color: String = "#888888"
) {
    @Transient
    val upgradeNameRegex = Regex(upgradeName)
}