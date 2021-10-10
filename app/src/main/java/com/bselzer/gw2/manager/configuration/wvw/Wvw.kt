package com.bselzer.gw2.manager.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName(value = "WorldVsWorld", namespace = "", prefix = "")
data class Wvw(
    /**
     * The World vs. World maps.
     */
    val maps: List<WvwMap> = emptyList()
)