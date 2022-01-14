package com.bselzer.gw2.manager.common.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwMapScroll(
    @XmlSerialName(value = "enabled", namespace = "", prefix = "")
    val enabled: Boolean = false,

    /**
     * The name of the map to scroll to.
     */
    @XmlSerialName(value = "map", namespace = "", prefix = "")
    val mapName: String = ""
)