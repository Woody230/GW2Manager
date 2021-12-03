package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.gw2.manager.common.configuration.common.Bound
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwMapLevel(
    @XmlSerialName(value = "zoom", namespace = "", prefix = "")
    val zoom: Int,

    @XmlSerialName(value = "Bound", namespace = "", prefix = "")
    val bound: Bound
)