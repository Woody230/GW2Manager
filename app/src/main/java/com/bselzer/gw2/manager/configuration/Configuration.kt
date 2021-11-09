package com.bselzer.gw2.manager.configuration

import com.bselzer.gw2.manager.configuration.wvw.Wvw
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName(value = "Configuration", namespace = "", prefix = "")
data class Configuration(
    /**
     * World vs. World
     */
    @XmlSerialName(value = "WorldVsWorld", namespace = "", prefix = "")
    val wvw: Wvw = Wvw()
)