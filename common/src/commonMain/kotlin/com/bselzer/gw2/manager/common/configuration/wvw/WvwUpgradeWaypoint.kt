package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.ktx.serialization.serializer.RegexPatternSerializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwUpgradeWaypoint(
    @XmlSerialName(value = "icon", namespace = "", prefix = "")
    val iconLink: String? = null,

    @Serializable(with = RegexPatternSerializer::class)
    @XmlSerialName(value = "upgrade", namespace = "", prefix = "")
    val upgradeName: Regex = Regex("^Build Waypoint$"),

    @XmlSerialName(value = "Guild", namespace = "", prefix = "")
    val guild: WvwGuildUpgradeWaypoint = WvwGuildUpgradeWaypoint()
)