package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.ktx.serialization.serializer.LenientDurationSerializer
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import kotlin.time.Duration

@Serializable
class WvwGuildImprovementTier(
    @XmlSerialName(value = "icon", namespace = "", prefix = "")
    override val iconLink: String = "",

    @Serializable(with = LenientDurationSerializer::class)
    @XmlSerialName(value = "hold", namespace = "", prefix = "")
    override val hold: Duration = Duration.INFINITE,

    @XmlSerialName(value = "Improvement", namespace = "", prefix = "")
    override val upgrades: List<WvwGuildUpgrade> = emptyList()
) : WvwGuildUpgradeTier