package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.gw2.v2.model.enumeration.wrapper.WorldName
import com.bselzer.gw2.v2.model.world.WorldId
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwWorld(
    @XmlSerialName(value = "id", namespace = "", prefix = "")
    @XmlElement(false)
    val id: WorldId,

    @XmlSerialName(value = "de", namespace = "", prefix = "")
    @XmlElement(false)
    val germanName: WorldName,

    @XmlSerialName(value = "en", namespace = "", prefix = "")
    @XmlElement(false)
    val englishName: WorldName,

    @XmlSerialName(value = "es", namespace = "", prefix = "")
    @XmlElement(false)
    val spanishName: WorldName,

    @XmlSerialName(value = "fr", namespace = "", prefix = "")
    @XmlElement(false)
    val frenchName: WorldName,
)