package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.gw2.manager.common.configuration.common.Size
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwGuildUpgrades(
    @XmlSerialName(value = "enabled", namespace = "", prefix = "")
    val enabled: Boolean = false,

    @XmlSerialName(value = "Size", namespace = "", prefix = "")
    val size: Size = Size(),

    @XmlSerialName(value = "TierSize", namespace = "", prefix = "")
    val tierSize: Size = Size(172, 172),

    @XmlSerialName(value = "ImprovementTier", namespace = "", prefix = "")
    val improvements: List<WvwGuildImprovementTier> = emptyList(),

    @XmlSerialName(value = "TacticTier", namespace = "", prefix = "")
    val tactics: List<WvwGuildTacticTier> = emptyList()
)