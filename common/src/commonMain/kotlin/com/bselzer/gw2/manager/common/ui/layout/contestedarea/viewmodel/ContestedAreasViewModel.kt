package com.bselzer.gw2.manager.common.ui.layout.contestedarea.viewmodel

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.configuration.wvw.WvwContestedAreasObjective
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.model.ContestedObjective
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.model.ContestedObjectives
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveType
import com.bselzer.gw2.v2.model.extension.wvw.count.contestedarea.ContestedAreasCount
import com.bselzer.gw2.v2.model.extension.wvw.count.contestedarea.ContestedAreasCountByOwner
import com.bselzer.gw2.v2.model.extension.wvw.objectiveOwnerCount
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.resource.strings.stringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.asImageUrl

class ContestedAreasViewModel(
    context: AppComponentContext,
    private val match: WvwMatch
) : ViewModel(context) {
    val contestedObjectives: List<ContestedObjectives>
        get() {
            val byOwner: List<ContestedAreasCountByOwner> = match.objectiveOwnerCount().contestedAreas.byOwner.filter(owners, objectiveTypes)
            return byOwner.map { counts -> counts.contestedObjectives() }
        }

    private fun ContestedAreasCountByOwner.contestedObjectives(): ContestedObjectives {
        val ppt = sumOf { count -> count.pointsPerTick }.coerceAtLeast(0)
        return ContestedObjectives(
            ppt = "+$ppt".desc(),
            color = owner.color(),
            objectives = map { count -> count.contestedObjective() }
        )
    }

    private fun ContestedAreasCount.contestedObjective(): ContestedObjective {
        val (link, color) = type.link(owner)

        // TODO in game the alpha would be at reduced opacity if there are no objectives -- should that be mimicked?
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