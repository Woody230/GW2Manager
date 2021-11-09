package com.bselzer.gw2.manager.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlDefault
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwMap(
    /**
     * Whether to bind the tiles to the bounds given by the associated level.
     */
    @XmlSerialName(value = "bounded", namespace = "", prefix = "")
    val isBounded: Boolean = false,

    /**
     * The default zoom level.
     */
    @XmlSerialName(value = "zoom", namespace = "", prefix = "")
    val defaultZoom: Int = 4,

    /**
     * The name of the region.
     */
    @XmlSerialName(value = "region", namespace = "", prefix = "")
    val regionName: String = "World vs. World",

    /**
     * How to handle scrolling.
     */
    @XmlSerialName(value = "Scroll", namespace = "", prefix = "")
    val scroll: WvwMapScroll = WvwMapScroll(),

    /**
     * How to handle zoom levels.
     */
    @XmlSerialName(value = "Level", namespace = "", prefix = "")
    val levels: List<WvwMapLevel> = emptyList()
)