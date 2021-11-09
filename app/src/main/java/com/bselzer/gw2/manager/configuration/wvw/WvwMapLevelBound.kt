package com.bselzer.gw2.manager.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwMapLevelBound(
    @XmlSerialName(value = "startX", namespace = "", prefix = "")
    val startX: Int = 0,

    @XmlSerialName(value = "startY", namespace = "", prefix = "")
    val startY: Int = 0,

    @XmlSerialName(value = "endX", namespace = "", prefix = "")
    val endX: Int = 0,

    @XmlSerialName(value = "endY", namespace = "", prefix = "")
    val endY: Int = 0
)