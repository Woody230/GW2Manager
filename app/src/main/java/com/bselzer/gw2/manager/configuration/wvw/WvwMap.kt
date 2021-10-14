package com.bselzer.gw2.manager.configuration.wvw

import com.bselzer.library.gw2.v2.model.enumeration.wvw.MapType
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName(value = "Map", namespace = "", prefix = "")
data class WvwMap(
    /**
     * The type of map.
     */
    @XmlSerialName("type", "", "")
    @XmlElement(false)
    val type: MapType,

    /**
     * The objectives.
     */
    val objectives: List<WvwMapObjective> = emptyList()
)