package com.bselzer.gw2.manager.configuration.wvw

import com.bselzer.gw2.manager.configuration.common.Size
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwUpgrades(
    @XmlSerialName(value = "enabled", namespace = "", prefix = "")
    val enabled: Boolean = false,

    @XmlSerialName(value = "Size", namespace = "", prefix = "")
    val defaultSize: Size = Size(32, 32),

    @XmlSerialName(value = "Progression", namespace = "", prefix="")
    val progression: List<WvwUpgradeProgression> = emptyList()
)