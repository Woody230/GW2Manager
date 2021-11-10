package com.bselzer.gw2.manager.configuration.wvw

import com.bselzer.gw2.manager.configuration.common.Bound
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwMapLevel(
    @XmlSerialName(value = "zoom", namespace = "", prefix = "")
    val zoom: Int = -1,

    @XmlSerialName(value = "Bound", namespace = "", prefix = "")
    val bound: Bound = Bound()
)