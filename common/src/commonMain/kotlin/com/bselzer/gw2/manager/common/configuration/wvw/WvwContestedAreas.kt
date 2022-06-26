package com.bselzer.gw2.manager.common.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwContestedAreas(
    @XmlSerialName(value = "Objective", namespace = "", prefix = "")
    val objectives: List<WvwContestedAreasObjective> = emptyList()
)