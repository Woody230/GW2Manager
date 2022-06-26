package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.gw2.v2.model.enumeration.WvwMapType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveType
import com.bselzer.ktx.serialization.serializer.DelimitedListSerializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwSupported(
    @Serializable(with = DelimitedListSerializer::class)
    @XmlSerialName(value = "objectiveTypes", namespace = "", prefix = "")
    @XmlElement(false)
    val objectiveTypes: List<WvwObjectiveType> = WvwObjectiveType.values().toList(),

    @Serializable(with = DelimitedListSerializer::class)
    @XmlSerialName(value = "owners", namespace = "", prefix = "")
    @XmlElement(false)
    val owners: List<WvwObjectiveOwner> = WvwObjectiveOwner.values().toList(),

    @Serializable(with = DelimitedListSerializer::class)
    @XmlSerialName(value = "mapTypes", namespace = "", prefix = "")
    @XmlElement(false)
    val mapTypes: List<WvwMapType> = WvwMapType.values().toList(),
)