package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.gw2.manager.common.configuration.common.Size
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwUpgradeProgressions(
    @XmlSerialName(value = "enabled", namespace = "", prefix = "")
    val enabled: Boolean = false,

    @XmlSerialName(value = "IndicatorSize", namespace = "", prefix = "")
    val indicatorSize: Size = Size(32, 32),

    @XmlSerialName(value = "IconSize", namespace = "", prefix = "")
    val iconSize: Size = Size(128, 128),

    @XmlSerialName(value = "TierIconSize", namespace = "", prefix = "")
    val tierIconSize: Size = Size(172, 172),

    @XmlSerialName(value = "Progression", namespace = "", prefix = "")
    val progression: List<WvwUpgradeProgression> = emptyList()
)