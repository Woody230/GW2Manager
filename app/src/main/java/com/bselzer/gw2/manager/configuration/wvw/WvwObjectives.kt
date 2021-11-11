package com.bselzer.gw2.manager.configuration.wvw

import com.bselzer.gw2.manager.configuration.common.Size
import com.bselzer.library.gw2.v2.model.enumeration.wvw.ObjectiveOwner
import com.bselzer.library.kotlin.extension.datetime.serialization.DurationSerializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
class WvwObjectives(
    @XmlSerialName(value = "Size", namespace = "", prefix = "")
    val defaultSize: Size = Size(64, 64),

    @XmlSerialName(value = "Color", namespace = "", prefix = "")
    val colors: List<WvwColor> = listOf(
        WvwColor(owner = ObjectiveOwner.NEUTRAL, type = "#888888"),
        WvwColor(owner = ObjectiveOwner.RED, type = "#ff0000"),
        WvwColor(owner = ObjectiveOwner.GREEN, type = "#00ff00"),
        WvwColor(owner = ObjectiveOwner.BLUE, type = "#0000ff")
    ),

    @XmlSerialName(value = "Objective", namespace = "", prefix = "")
    val objectives: List<WvwObjective> = emptyList(),

    @XmlSerialName(value = "Immunity", namespace = "", prefix = "")
    val immunity: WvwObjectivesImmunity = WvwObjectivesImmunity(),

    @XmlSerialName(value = "Upgrades", namespace = "", prefix = "")
    val upgrades: WvwUpgrades = WvwUpgrades()
)