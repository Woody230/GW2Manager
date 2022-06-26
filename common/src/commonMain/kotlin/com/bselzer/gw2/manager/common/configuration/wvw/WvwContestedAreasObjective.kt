package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveType
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwContestedAreasObjective(
    @XmlSerialName(value = "type", namespace = "", prefix = "")
    @XmlElement(false)
    val type: WvwObjectiveType = WvwObjectiveType.GENERIC,

    @XmlSerialName(value = "blue", namespace = "", prefix = "")
    val blueLink: String = "",

    @XmlSerialName(value = "green", namespace = "", prefix = "")
    val greenLink: String = "",

    @XmlSerialName(value = "red", namespace = "", prefix = "")
    val redLink: String = "",

    @XmlSerialName(value = "neutral", namespace = "", prefix = "")
    val neutralLink: String = "",
)