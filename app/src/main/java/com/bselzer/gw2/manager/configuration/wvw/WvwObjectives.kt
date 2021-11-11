package com.bselzer.gw2.manager.configuration.wvw

import com.bselzer.gw2.manager.configuration.common.Size
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwObjectives(
    @XmlSerialName(value = "Size", namespace = "", prefix = "")
    val defaultSize: Size = Size(),

    @XmlSerialName(value = "Color", namespace = "", prefix = "")
    val colors: List<WvwColor> = emptyList(),

    @XmlSerialName(value = "Objective", namespace = "", prefix = "")
    val objectives: List<WvwObjective> = emptyList()
)