package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.ktx.serialization.serializer.DurationSerializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import kotlin.time.Duration

@Serializable
class WvwGuildTacticTier(
    @XmlSerialName(value = "icon", namespace = "", prefix = "")
    override val iconLink: String = "",

    @Serializable(with = DurationSerializer::class)
    @XmlSerialName(value = "hold", namespace = "", prefix = "")
    override val hold: Duration = Duration.INFINITE,

    @XmlSerialName(value = "Tactic", namespace = "", prefix = "")
    override val upgrades: List<WvwGuildUpgrade> = emptyList()
) : WvwGuildUpgradeTier