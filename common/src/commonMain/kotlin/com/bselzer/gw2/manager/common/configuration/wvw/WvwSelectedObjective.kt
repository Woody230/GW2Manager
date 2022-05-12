package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.ktx.datetime.format.DateTimeFormatter
import com.bselzer.ktx.datetime.format.PatternDateTimeFormatter
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
) {
    @Transient
    val dateFormatter: DateTimeFormatter = PatternDateTimeFormatter(dateFormat)
}