package com.bselzer.gw2.manager.common.configuration

import com.bselzer.gw2.manager.common.configuration.wvw.Wvw
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName(value = "Configuration", namespace = "", prefix = "")
data class AppConfiguration(
    /**
     * World vs. World
     */
    @XmlSerialName(value = "WorldVsWorld", namespace = "", prefix = "")
    override val wvw: Wvw = Wvw(),

    /**
     * The alpha (0-1 scaled) for when an image needs to be more transparent from full opacity.
     */
    @XmlSerialName(value = "transparency", namespace = "", prefix = "")
    override val transparency: Float = 0.75f
) : Configuration