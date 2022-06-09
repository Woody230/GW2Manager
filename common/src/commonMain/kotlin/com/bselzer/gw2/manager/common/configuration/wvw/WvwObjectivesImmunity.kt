package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.ktx.datetime.serialization.DurationSerializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
class WvwObjectivesImmunity(
    @XmlSerialName(value = "enabled", namespace = "", prefix = "")
    val enabled: Boolean = false,

    @Serializable(with = DurationSerializer::class)
    @XmlSerialName(value = "duration", namespace = "", prefix = "")
    @XmlElement(false)
    val defaultDuration: Duration? = null,
)