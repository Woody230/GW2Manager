package com.bselzer.gw2.manager.configuration.wvw

import com.bselzer.library.gw2.v2.model.enumeration.wvw.ObjectiveType
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwObjective(
    @XmlSerialName(value = "type", namespace = "", prefix = "")
    @XmlElement(false)
    val type: ObjectiveType = ObjectiveType.GENERIC,

    @XmlSerialName(value = "icon", namespace = "", prefix = "")
    val defaultIconLink: String? = null,

    @XmlSerialName(value = "Size", namespace = "", prefix = "")
    val size: WvwSize? = null
)