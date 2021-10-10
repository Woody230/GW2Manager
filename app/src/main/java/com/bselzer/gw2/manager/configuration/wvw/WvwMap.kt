package com.bselzer.gw2.manager.configuration.wvw

import com.bselzer.library.gw2.v2.model.enumeration.wvw.MapType
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName(value = "Map", namespace = "", prefix = "")
data class WvwMap(
    /**
     * The vertical size within the image.
     */
    @XmlSerialName("height", "", "")
    val height: Int = 0,

    /**
     * The type of map.
     */
    @XmlSerialName("type", "", "")
    @XmlElement(false)
    val type: MapType,

    /**
     * The horizontal size within the image.
     */
    @XmlSerialName("width", "", "")
    val width: Int = 0,

    /**
     * The horizontal position within the image.
     */
    @XmlSerialName("x", "", "")
    val x: Int,

    /**
     * The vertical position within the image.
     */
    @XmlSerialName("y", "", "")
    val y: Int,

    /**
     * The objectives.
     */
    val objectives: List<WvwMapObjective> = emptyList()
)