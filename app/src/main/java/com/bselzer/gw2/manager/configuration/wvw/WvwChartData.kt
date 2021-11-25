package com.bselzer.gw2.manager.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwChartData {
    /**
     * The text size in SP.
     */
    @XmlSerialName(value = "textSize", namespace = "", prefix = "")
    val textSize: Float = 24f
}