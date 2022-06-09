package com.bselzer.gw2.manager.common.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwMapLevel(
    @XmlSerialName(value = "zoom", namespace = "", prefix = "")
    val zoom: Int,

    @XmlSerialName(value = "startX", namespace = "", prefix = "")
    val startX: Int,

    @XmlSerialName(value = "startY", namespace = "", prefix = "")
    val startY: Int,

    @XmlSerialName(value = "endX", namespace = "", prefix = "")
    val endX: Int,

    @XmlSerialName(value = "endY", namespace = "", prefix = "")
    val endY: Int
)