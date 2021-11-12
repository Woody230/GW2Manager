package com.bselzer.gw2.manager.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwSelectedObjective(
    @XmlSerialName(value = "dateFormat", namespace = "", prefix = "")
    val dateFormat: String = "hh:mm a",

    /**
     * The text size in SP.
     */
    @XmlSerialName(value = "textSize", namespace = "", prefix = "")
    val textSize: Float = 16f,
)