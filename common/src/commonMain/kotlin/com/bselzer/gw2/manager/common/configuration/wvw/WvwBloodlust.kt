package com.bselzer.gw2.manager.common.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwBloodlust(
    @XmlSerialName(value = "icon", namespace = "", prefix = "")
    val iconLink: String = ""
)