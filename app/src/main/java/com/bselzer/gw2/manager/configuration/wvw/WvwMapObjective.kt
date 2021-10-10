package com.bselzer.gw2.manager.configuration.wvw

import com.bselzer.library.gw2.v2.model.enumeration.wvw.ObjectiveType
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName(value = "Objective", namespace = "", prefix = "")
data class WvwMapObjective(
    /**
     * A user-friendly identifier for representing this objective within the map.
     */
    @XmlSerialName("code", "", "")
    val code: String = "",

    /**
     * The identifier.
     */
    @XmlSerialName("id", "", "")
    val id: Int,

    /**
     * The type of objective.
     */
    @XmlSerialName("type", "", "")
    @XmlElement(false)
    val type: ObjectiveType,

    /**
     * The horizontal position within the image.
     */
    @XmlSerialName("x", "", "")
    val x: Int,

    /**
     * The vertical position within the image.
     */
    @XmlSerialName("y", "", "")
    val y: Int
)