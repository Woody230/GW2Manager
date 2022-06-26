package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveType
import com.bselzer.ktx.serialization.serializer.DelimitedListSerializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwGuildUpgrade(
    @XmlSerialName(value = "id", namespace = "", prefix = "")
    val id: Int,

    @XmlSerialName(value = "name", namespace = "", prefix = "")
    val name: String = "",

    /**
     * The names of the objective types that can use this upgrade.
     */
    @Serializable(with = DelimitedListSerializer::class)
    @XmlSerialName(value = "availability", namespace = "", prefix = "")
    @XmlElement(false)
    val availability: List<WvwObjectiveType>
)