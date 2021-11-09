package com.bselzer.gw2.manager.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class Wvw(
    @XmlSerialName(value = "Objectives", namespace = "", prefix = "")
    val objectives: WvwObjectives = WvwObjectives()
)