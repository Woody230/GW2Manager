package com.bselzer.gw2.manager.common.configuration

import androidx.compose.ui.graphics.DefaultAlpha
import com.bselzer.gw2.manager.common.configuration.wvw.Wvw
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName(value = "Configuration", namespace = "", prefix = "")
data class Configuration(
    /**
     * World vs. World
     */
    @XmlSerialName(value = "WorldVsWorld", namespace = "", prefix = "")
    val wvw: Wvw = Wvw(),

    /**
     * The alpha (0-1 scaled) for when an image needs to be more transparent from full opacity.
     */
    @XmlSerialName(value = "transparency", namespace = "", prefix = "")
    val transparency: Float = 0.75f
) {
    /**
     * @return [DefaultAlpha] if the [condition] is true, otherwise the [transparency]
     */
    fun alpha(condition: Boolean): Float = if (condition) DefaultAlpha else transparency
}