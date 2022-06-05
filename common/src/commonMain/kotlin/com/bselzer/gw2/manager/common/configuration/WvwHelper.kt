package com.bselzer.gw2.manager.common.configuration

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.configuration.wvw.Wvw
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.extension.enumValueOrNull
import com.bselzer.gw2.v2.model.extension.wvw.linkedWorlds
import com.bselzer.gw2.v2.model.extension.wvw.mainWorld
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.ktx.compose.ui.graphics.color.Hex
import com.bselzer.ktx.compose.ui.graphics.color.color
import com.bselzer.ktx.function.objects.userFriendly
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.Instant

object WvwHelper {
    /**
     * @return the objective from the configuration associated with the endpoint objective
     */
    fun Wvw.objective(objective: WvwObjective?) = objective?.let {
        objectives.objectives.firstOrNull { it.type == objective.type.enumValueOrNull() }
    }

    /**
     * @return the color associated with the endpoint objective
     */
    fun Wvw.color(objective: WvwMapObjective?): Color = color(objective?.owner?.enumValueOrNull())

    /**
     * @return the color associated with an objective [owner]
     */
    fun Wvw.color(owner: WvwObjectiveOwner?, default: String = "#888888") = Hex(objectives.hex(owner = owner ?: WvwObjectiveOwner.NEUTRAL, default = default)).color()

    /**
     * @return the date/time instant to a displayable formatted string
     */
    fun Wvw.selectedDateFormatted(instant: Instant): String = objectives.selected.dateFormatter.format(instant)

    /**
     * @return the displayable names for the linked worlds associated with the objective [owner]
     */
    fun Collection<World>.displayableLinkedWorlds(match: WvwMatch?, owner: WvwObjectiveOwner): String {
        val worlds = this
        val linkedWorlds = match?.linkedWorlds(owner)?.mapNotNull { worldId -> worlds.firstOrNull { world -> world.id == worldId }?.name }

        // Default to using the owner if there are no worlds.
        if (linkedWorlds.isNullOrEmpty()) return owner.userFriendly()

        // Make sure that the main world is first.
        val mainWorld = match.mainWorld(owner)?.run { worlds.firstOrNull { world -> world.id == this }?.name }
        val sortedWorlds = if (mainWorld == null) linkedWorlds else linkedWorlds.toMutableList().apply { remove(mainWorld); add(0, mainWorld) }
        return sortedWorlds.joinToString(separator = "/")
    }

    /**
     * @return the [StringResource] representing the objective owner
     */
    fun WvwObjectiveOwner.stringResource(): StringDesc = when (this) {
        WvwObjectiveOwner.RED -> KtxResources.strings.red
        WvwObjectiveOwner.BLUE -> KtxResources.strings.blue
        WvwObjectiveOwner.GREEN -> KtxResources.strings.green
        WvwObjectiveOwner.NEUTRAL -> KtxResources.strings.gray
    }.desc()
}