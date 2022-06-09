package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveType
import com.bselzer.ktx.datetime.serialization.DurationSerializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import kotlin.time.Duration

@Serializable
class WvwObjective(
    @XmlSerialName(value = "type", namespace = "", prefix = "")
    @XmlElement(false)
    val type: WvwObjectiveType = WvwObjectiveType.GENERIC,

    @XmlSerialName(value = "icon", namespace = "", prefix = "")
    val defaultIconLink: String? = null,

    @Serializable(with = DurationSerializer::class)
    @XmlSerialName(value = "immunity", namespace = "", prefix = "")
    @XmlElement(false)
    val immunity: Duration? = null
)