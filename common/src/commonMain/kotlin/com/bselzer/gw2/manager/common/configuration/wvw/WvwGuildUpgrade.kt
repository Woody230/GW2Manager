package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.gw2.v2.model.enumeration.wvw.ObjectiveType
import com.bselzer.ktx.serialization.function.enumValueOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwGuildUpgrade(
    @XmlSerialName(value = "id", namespace = "", prefix = "")
    val id: Int,

    @XmlSerialName(value = "name", namespace = "", prefix = "")
    val name: String = "",

    /**
     * The names of the objective types that can use this upgrade.
     */
    @XmlSerialName(value = "availability", namespace = "", prefix = "")
    val availability: String = ""
) {
    /**
     * The types of objectives that can use this upgrades.
     */
    @Transient
    val objectiveTypes: Collection<ObjectiveType> = availability.split(',').mapNotNull { type -> type.enumValueOrNull<ObjectiveType>() }
}