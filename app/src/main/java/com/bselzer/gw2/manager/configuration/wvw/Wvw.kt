package com.bselzer.gw2.manager.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class Wvw(
    @XmlSerialName(value = "Objectives", namespace = "", prefix = "")
    val objectives: WvwObjectives = WvwObjectives(),

    @XmlSerialName(value = "Map", namespace = "", prefix = "")
    val map: WvwMap = WvwMap(),

    @XmlSerialName(value = "Bloodlust", namespace = "", prefix = "")
    val bloodlust: WvwBloodlust = WvwBloodlust(),

    @XmlSerialName(value = "Chart", namespace = "", prefix = "")
    val chart: WvwChart = WvwChart()
)