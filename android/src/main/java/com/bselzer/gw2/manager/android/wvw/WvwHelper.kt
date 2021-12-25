package com.bselzer.gw2.manager.android.wvw

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.configuration.wvw.Wvw
import com.bselzer.gw2.v2.model.enumeration.extension.wvw.owner
import com.bselzer.gw2.v2.model.enumeration.extension.wvw.type
import com.bselzer.gw2.v2.model.enumeration.wvw.ObjectiveOwner
import com.bselzer.gw2.v2.model.extension.wvw.linkedWorlds
import com.bselzer.gw2.v2.model.extension.wvw.mainWorld
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.wvw.match.WvwMapObjective
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.ktx.compose.ui.style.Hex
import com.bselzer.ktx.compose.ui.style.color
import com.bselzer.ktx.function.objects.userFriendly
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime

object WvwHelper {
    /**
     * @return the objective from the configuration associated with the endpoint objective
     */
    fun Wvw.objective(objective: WvwObjective?) = objective?.let {
        objectives.objectives.firstOrNull { it.type == objective.type() }
    }

    /**
     * @return the color associated with the endpoint objective
     */
    fun Wvw.color(objective: WvwMapObjective?): Color = color(objective?.owner() ?: ObjectiveOwner.NEUTRAL)

    /**
     * @return the color associated with an objective [owner]
     */
    fun Wvw.color(owner: ObjectiveOwner, default: String = "#888888") = Hex(objectives.hex(owner = owner, default = default)).color()

    /**
     * @return the date/time instant to a displayable formatted string
     */
    fun Wvw.selectedDateFormatted(instant: Instant): String {
        // TODO kotlinx.datetime please support formatting -- https://github.com/Kotlin/kotlinx-datetime/issues?q=label%3Aformatters+
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime()
        return objectives.selected.dateFormatter.format(localDate)
    }

    /**
     * @return the displayable names for the linked worlds associated with the objective [owner]
     */
    fun Collection<World>.displayableLinkedWorlds(match: WvwMatch?, owner: ObjectiveOwner): String {
        val worlds = this
        val linkedWorlds = match?.linkedWorlds(owner)?.mapNotNull { worldId -> worlds.firstOrNull { world -> world.id == worldId }?.name }

        // Default to using the owner if there are no worlds.
        if (linkedWorlds.isNullOrEmpty()) return owner.userFriendly()

        // Make sure that the main world is first.
        val mainWorld = match.mainWorld(owner)?.run { worlds.firstOrNull { world -> world.id == this }?.name }
        val sortedWorlds = if (mainWorld == null) linkedWorlds else linkedWorlds.toMutableList().apply { remove(mainWorld); add(0, mainWorld) }
        return sortedWorlds.joinToString(separator = "/")
    }
}