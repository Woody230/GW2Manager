package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.gw2.manager.common.configuration.common.Size
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwUpgradeProgression(
    @XmlSerialName(value = "icon", namespace = "", prefix = "")
    val iconLink: String? = null,

    @XmlSerialName(value = "Size", namespace = "", prefix = "")
    val size: Size? = null,
)