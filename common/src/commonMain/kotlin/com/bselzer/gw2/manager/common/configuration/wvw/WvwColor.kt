package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.gw2.v2.model.enumeration.wvw.ObjectiveOwner
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
    val owner: ObjectiveOwner = ObjectiveOwner.NEUTRAL,

    /**
     * The color content as hex.
     */
    @XmlSerialName(value = "type", namespace = "", prefix = "")
    val type: String = "#000000"
)