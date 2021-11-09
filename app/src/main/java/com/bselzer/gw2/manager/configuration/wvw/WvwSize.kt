package com.bselzer.gw2.manager.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwSize (
    @XmlSerialName(value = "width", namespace = "", prefix = "")
    val width: Int = 32,

    @XmlSerialName(value = "height", namespace = "", prefix = "")
    val height: Int = 32,
)