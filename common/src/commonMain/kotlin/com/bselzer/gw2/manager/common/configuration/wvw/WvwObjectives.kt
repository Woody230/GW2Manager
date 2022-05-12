package com.bselzer.gw2.manager.common.configuration.wvw

import com.bselzer.gw2.manager.common.configuration.common.Size
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
class WvwObjectives(
    /**
     * The objective size to use when one is not specified in [objectives].
     */
    @XmlSerialName(value = "Size", namespace = "", prefix = "")
    val defaultSize: Size = Size(64, 64),

    /**
     * The colors associated with each owner.
     */
    @XmlSerialName(value = "Color", namespace = "", prefix = "")
    val colors: List<WvwColor> = listOf(
        WvwColor(owner = WvwObjectiveOwner.NEUTRAL, type = "#888888"),
        WvwColor(owner = WvwObjectiveOwner.RED, type = "#ff0000"),
        WvwColor(owner = WvwObjectiveOwner.GREEN, type = "#00ff00"),
        WvwColor(owner = WvwObjectiveOwner.BLUE, type = "#0000ff")
    ),

    /**
     * Information specific to each objective type.
     */
    @XmlSerialName(value = "Objective", namespace = "", prefix = "")
    val objectives: List<WvwObjective> = emptyList(),

    /**
     * Information related to the objective selected by the user.
     */
    @XmlSerialName(value = "Selected", namespace = "", prefix = "")
    val selected: WvwSelectedObjective = WvwSelectedObjective(),

    /**
     * Information related to objective lords being immune to damage and preventing capture.
     */
    @XmlSerialName(value = "Immunity", namespace = "", prefix = "")
    val immunity: WvwObjectivesImmunity = WvwObjectivesImmunity(),

    /**
     * Information related to the upgrade tiers: secured/reinforced/fortified
     */
    @XmlSerialName(value = "Upgrades", namespace = "", prefix = "")
    val progressions: WvwUpgradeProgressions = WvwUpgradeProgressions(),

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
) {
    /**
     * @return the color associated with the [owner], or the [default] if it does not exist
     */
    fun hex(owner: WvwObjectiveOwner, default: String): String = hex(owner) ?: default

    /**
     * @return the color associated with the [owner], or null if it does not exist
     */
    fun hex(owner: WvwObjectiveOwner): String? = colors.firstOrNull { color -> color.owner == owner }?.type
}