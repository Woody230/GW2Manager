package com.bselzer.gw2.manager.common.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwGuildUpgrades(
    @XmlSerialName(value = "enabled", namespace = "", prefix = "")
    val enabled: Boolean = false,

    @XmlSerialName(value = "ImprovementTier", namespace = "", prefix = "")
    val improvements: List<WvwGuildImprovementTier> = emptyList(),

    @XmlSerialName(value = "TacticTier", namespace = "", prefix = "")
    val tactics: List<WvwGuildTacticTier> = emptyList()
)