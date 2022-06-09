package com.bselzer.gw2.manager.common.configuration.wvw

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwUpgradeWaypoint(
    @XmlSerialName(value = "icon", namespace = "", prefix = "")
    val iconLink: String? = null,

    @XmlSerialName(value = "upgrade", namespace = "", prefix = "")
    val upgradeName: String = "^Build Waypoint$",

    @XmlSerialName(value = "Guild", namespace = "", prefix = "")
    val guild: WvwGuildUpgradeWaypoint = WvwGuildUpgradeWaypoint()
) {
    @Transient
    val upgradeNameRegex = Regex(upgradeName)
}