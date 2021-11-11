package com.bselzer.gw2.manager.configuration.common

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class Size(
    @XmlSerialName(value = "width", namespace = "", prefix = "")
    val width: Int = 64,

    @XmlSerialName(value = "height", namespace = "", prefix = "")
    val height: Int = 64,
)