package com.bselzer.gw2.manager.common.configuration.wvw

import androidx.compose.ui.graphics.Color
import com.bselzer.ktx.compose.ui.graphics.color.Hex
import com.bselzer.ktx.compose.ui.graphics.color.color
import com.bselzer.ktx.serialization.serializer.ColorSerializer
import com.bselzer.ktx.serialization.serializer.RegexPatternSerializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwGuildUpgradeWaypoint(
    @Serializable(with = RegexPatternSerializer::class)
    @XmlSerialName(value = "upgrade", namespace = "", prefix = "")
    val upgradeName: Regex = Regex("^Emergency Waypoint$"),

    /**
     * The color transformation as hex.
     */
    @Serializable(with = ColorSerializer::class)
    @XmlSerialName(value = "color", namespace = "", prefix = "")
    val color: Color = Hex("#888888").color()
)