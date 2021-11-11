package com.bselzer.gw2.manager.configuration.wvw

import com.bselzer.gw2.manager.configuration.common.Size
import com.bselzer.library.gw2.v2.model.enumeration.wvw.ObjectiveType
import com.bselzer.library.kotlin.extension.datetime.serialization.DurationSerializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
class WvwObjective(
    @XmlSerialName(value = "type", namespace = "", prefix = "")
    @XmlElement(false)
    val type: ObjectiveType = ObjectiveType.GENERIC,

    @XmlSerialName(value = "icon", namespace = "", prefix = "")
    val defaultIconLink: String? = null,

    @XmlSerialName(value = "Size", namespace = "", prefix = "")
    val size: Size? = null,

    @Serializable(with = DurationSerializer::class)
    @XmlSerialName(value = "immunity", namespace = "", prefix = "")
    @XmlElement(false)
    val immunity: Duration? = null
)