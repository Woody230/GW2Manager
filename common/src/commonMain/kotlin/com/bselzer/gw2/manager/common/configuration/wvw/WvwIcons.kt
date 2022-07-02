package com.bselzer.gw2.manager.common.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwIcons(
    @XmlSerialName(value = "home", namespace = "", prefix = "")
    val home: String = "",

    @XmlSerialName(value = "pointsPerTick", namespace = "", prefix = "")
    val pointsPerTick: String = "",

    @XmlSerialName(value = "warScore", namespace = "", prefix = "")
    val warScore: String = "",

    @XmlSerialName(value = "bloodlust", namespace = "", prefix = "")
    val bloodlust: String = "",
)