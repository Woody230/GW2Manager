package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.gw2.manager.common.configuration.common.Size
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwGuildClaim(
    @XmlSerialName(value = "enabled", namespace = "", prefix = "")
    val enabled: Boolean = false,

    @XmlSerialName(value = "icon", namespace = "", prefix = "")
    val iconLink: String? = null,

    @XmlSerialName(value = "Size", namespace = "", prefix = "")
    val size: Size = Size()
)