package com.bselzer.gw2.manager.common.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwUpgradeProgression(
    /**
     * The link to the small indicator icon.
     */
    @XmlSerialName(value = "indicator", namespace = "", prefix = "")
    val indicatorLink: String = "",

    /**
     * The link to the large structure icon.
     */
    @XmlSerialName(value = "icon", namespace = "", prefix = "")
    val iconLink: String = ""
)