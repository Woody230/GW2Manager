package com.bselzer.gw2.manager.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwObjectives(
    @XmlSerialName(value = "Size", namespace = "", prefix = "")
    val defaultSize: WvwSize = WvwSize(),

    @XmlSerialName(value = "Color", namespace = "", prefix = "")
    val colors: List<WvwColor> = emptyList(),

    @XmlSerialName(value = "Objective", namespace = "", prefix = "")
    val objectives: List<WvwObjective> = emptyList()
)