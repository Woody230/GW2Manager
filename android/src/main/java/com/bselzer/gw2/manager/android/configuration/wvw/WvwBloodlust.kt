package com.bselzer.gw2.manager.android.configuration.wvw

import com.bselzer.gw2.manager.android.configuration.common.Size
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwBloodlust(
    @XmlSerialName(value = "enabled", namespace = "", prefix = "")
    val enabled: Boolean = false,

    @XmlSerialName(value = "icon", namespace = "", prefix = "")
    val iconLink: String = "",

    @XmlSerialName(value = "Size", namespace = "", prefix = "")
    val size: Size = Size()
)