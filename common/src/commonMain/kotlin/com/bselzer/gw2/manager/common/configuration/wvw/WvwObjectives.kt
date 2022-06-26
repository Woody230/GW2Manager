package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.ktx.compose.ui.graphics.color.Hex
import com.bselzer.ktx.compose.ui.graphics.color.color
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwObjectives(
    /**
     * The colors associated with each owner.
     */
    @XmlSerialName(value = "Color", namespace = "", prefix = "")
    val colors: List<WvwColor> = listOf(
        WvwColor(owner = WvwObjectiveOwner.NEUTRAL, type = Hex("#888888").color()),
        WvwColor(owner = WvwObjectiveOwner.RED, type = Hex("#ff0000").color()),
        WvwColor(owner = WvwObjectiveOwner.GREEN, type = Hex("#00ff00").color()),
        WvwColor(owner = WvwObjectiveOwner.BLUE, type = Hex("#0000ff").color())
    ),

    /**
     * Information specific to each objective type.
     */
    @XmlSerialName(value = "Objective", namespace = "", prefix = "")
    val objectives: List<WvwObjective> = emptyList(),

    /**
     * Information related to the upgrade tiers: secured/reinforced/fortified
     */
    @XmlSerialName(value = "Progression", namespace = "", prefix = "")
    val progressions: List<WvwUpgradeProgression> = emptyList(),

    /**
     * Information related to guild upgrade tier progression.
     */
    @XmlSerialName(value = "GuildUpgrades", namespace = "", prefix = "")
    val guildUpgrades: WvwGuildUpgrades = WvwGuildUpgrades(),

    /**
     * Information related to the waypoint upgrade/tactic.
     */
    @XmlSerialName(value = "Waypoint", namespace = "", prefix = "")
    val waypoint: WvwUpgradeWaypoint = WvwUpgradeWaypoint(),

    /**
     * Information related to guilds claiming objectives.
     */
    @XmlSerialName(value = "Claim", namespace = "", prefix = "")
    val claim: WvwGuildClaim = WvwGuildClaim(),
)