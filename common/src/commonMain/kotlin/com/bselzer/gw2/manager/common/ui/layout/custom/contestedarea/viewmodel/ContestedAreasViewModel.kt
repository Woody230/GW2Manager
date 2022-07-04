package com.bselzer.gw2.manager.common.ui.layout.custom.contestedarea.viewmodel

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.configuration.wvw.WvwContestedAreasObjective
import com.bselzer.gw2.manager.common.dependency.ViewModelDependencies
import com.bselzer.gw2.manager.common.ui.layout.custom.contestedarea.model.ContestedObjective
import com.bselzer.gw2.manager.common.ui.layout.custom.contestedarea.model.ContestedPointsPerTick
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveType
import com.bselzer.gw2.v2.model.extension.wvw.count.contestedarea.ContestedAreas
import com.bselzer.gw2.v2.model.extension.wvw.count.contestedarea.ContestedAreasCount
import com.bselzer.gw2.v2.model.extension.wvw.count.contestedarea.ContestedAreasCountByOwner
import com.bselzer.gw2.v2.resource.strings.stringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.asImageUrl

interface ContestedAreasViewModel : ViewModelDependencies {
    /**
     * The contested areas to create the [contestedObjectives] for.
     */
    val contestedAreas: ContestedAreas

    /**
     * The counts for the objectives with a [WvwObjectiveType] in [objectiveTypes] associated with a specific [WvwObjectiveOwner] in [owners].
     *
     * The objectives are grouped by type and should consequently be laid out in columns.
     */
    val contestedObjectives: List<List<ContestedObjective>>
        get() {
            val byType = contestedAreas.byType.filter(objectiveTypes, owners).map { byType -> byType.counts }
            return byType.map { counts -> counts.map { count -> count.contestedObjective() } }
        }

    /**
     * The points per tick for the objectives with a [WvwObjectiveType] in [objectiveTypes] associated with a specific [WvwObjectiveOwner] in [owners].
     */
    val pointsPerTick: List<ContestedPointsPerTick>
        get() = contestedAreas.byOwner.filter(owners, objectiveTypes).map { byOwner -> byOwner.pointsPerTick() }

    private fun ContestedAreasCountByOwner.pointsPerTick(): ContestedPointsPerTick {
        val ppt = sumOf { count -> count.pointsPerTick }.coerceAtLeast(0)
        return ContestedPointsPerTick(
            ppt = "+$ppt".desc(),
            color = owner.color(),
        )
    }

    private fun ContestedAreasCount.contestedObjective(): ContestedObjective {
        val (link, color) = type.link(owner)

        // TODO in game the alpha would be at reduced opacity if there are no objectives -- should that be mimicked?
        // TODO use yellow color used in game?
        return ContestedObjective(
            link = link,
            color = color,
            description = type.stringDesc(),
            count = "x$size".desc()
        )
    }

    private fun WvwObjectiveType.link(owner: WvwObjectiveOwner): Pair<ImageDesc, Color?> {
        val objective = configuration.wvw.contestedAreas.objectives.firstOrNull { objective -> objective.type == this } ?: return defaultLink(owner)

        // If using the default color, then use the same color as it is in game.
        return if (owner.hasDefaultColor()) {
            objective.link(owner) to null
        } else {
            objective.link(WvwObjectiveOwner.NEUTRAL) to owner.color()
        }
    }

    private fun WvwContestedAreasObjective.link(owner: WvwObjectiveOwner) = when (owner) {
        WvwObjectiveOwner.BLUE -> blueLink
        WvwObjectiveOwner.GREEN -> greenLink
        WvwObjectiveOwner.RED -> redLink
        WvwObjectiveOwner.NEUTRAL -> neutralLink
    }.asImageUrl()

    private fun WvwObjectiveType.defaultLink(owner: WvwObjectiveOwner): Pair<ImageDesc, Color?> {
        val objective = configuration.wvw.objectives.objectives.firstOrNull { objective -> objective.type == this }
        val link = objective?.defaultIconLink ?: ""
        return link.asImageUrl() to owner.color()
    }
}