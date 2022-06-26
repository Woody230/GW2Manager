package com.bselzer.gw2.manager.common.configuration.wvw

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.ktx.serialization.compose.serializer.ColorSerializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwColor(
    /**
     * The owner of the objective.
     */
    @XmlSerialName(value = "owner", namespace = "", prefix = "")
    @XmlElement(false)
    val owner: WvwObjectiveOwner = WvwObjectiveOwner.NEUTRAL,

    /**
     * The color content as hex.
     */
    @Serializable(with = ColorSerializer::class)
    @XmlSerialName(value = "type", namespace = "", prefix = "")
    val type: Color = Color.Yellow
)