package com.bselzer.gw2.manager.common.configuration.wvw

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwGuildTacticTier(
    @XmlSerialName(value = "icon", namespace = "", prefix = "")
    override val iconLink: String = "",

    @XmlSerialName(value = "hold", namespace = "", prefix = "")
    override val hold: String = "",

    @XmlSerialName(value = "Tactic", namespace = "", prefix = "")
    override val upgrades: List<WvwGuildUpgrade> = emptyList()
) : WvwGuildUpgradeTier