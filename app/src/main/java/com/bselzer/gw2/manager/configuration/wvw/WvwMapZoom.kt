package com.bselzer.gw2.manager.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwMapZoom(
    @XmlSerialName(value = "default", namespace = "", prefix = "")
    val default: Int = 4,

    @XmlSerialName(value = "min", namespace = "", prefix = "")
    val min: Int = 4,

    @XmlSerialName(value = "max", namespace = "", prefix = "")
    val max: Int = 4
) {
    init {
        if (min > max) throw IllegalArgumentException("Minimum zoom of $min must be equal to or less than the maximum zoom of $max.")
        if (default < min) throw IllegalArgumentException("Default zoom of $default must be equal to or greater than the minimum zoom of $min.")
        if (default > max) throw IllegalArgumentException("Default zoom of $default must be equal to or less than the minimum zoom of $max.")
    }
}