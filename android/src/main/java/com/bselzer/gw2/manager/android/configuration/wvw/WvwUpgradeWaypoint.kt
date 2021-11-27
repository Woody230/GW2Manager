package com.bselzer.gw2.manager.android.configuration.wvw

import com.bselzer.gw2.manager.android.configuration.common.Size
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwUpgradeWaypoint(
    @XmlSerialName(value = "enabled", namespace = "", prefix = "")
    val enabled: Boolean = false,

    @XmlSerialName(value = "icon", namespace = "", prefix = "")
    val iconLink: String? = null,

    @XmlSerialName(value = "Size", namespace = "", prefix = "")
    val size: Size = Size(),

    @XmlSerialName(value = "upgrade", namespace = "", prefix = "")
    val upgradeName: String = "^Build Waypoint$",

    @XmlSerialName(value = "Guild", namespace = "", prefix = "")
    val guild: WvwGuildUpgradeWaypoint = WvwGuildUpgradeWaypoint()
) {
    @Transient
    val upgradeNameRegex = Regex(upgradeName)
}