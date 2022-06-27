package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.match

import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.borderlands.viewmodel.BorderlandsViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.Progress
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.Progression
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.extension.wvw.count.ObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.count.WvwMatchObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.objectiveOwnerCount
import com.bselzer.gw2.v2.model.wvw.map.WvwMap
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.gw2.v2.resource.strings.stringDesc
import com.bselzer.ktx.function.collection.addTo
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.asImageDesc
import dev.icerock.moko.resources.desc.image.asImageUrl

class StatisticsViewModel(
    context: AppComponentContext,
    match: WvwMatch
) : BorderlandsViewModel<List<Progression>>(context, match) {
    override val defaultData: List<Progression> = emptyList()

    override val overviewData: (WvwMatch) -> List<Progression> = { match ->
        with(match.objectiveOwnerCount()) {
            buildList {
                add(vpProgression())
                add(pptProgression())
                add(scoreProgression())
                add(killProgression())
                add(deathProgression())
                addAll(contestedAreaProgressions())
            }
        }
    }

    override val borderlandData: (WvwMap) -> List<Progression> = { map ->
        with(map.objectiveOwnerCount()) {
            buildList {
                add(pptProgression())
                add(scoreProgression())
                add(killProgression())
                add(deathProgression())
                addAll(contestedAreaProgressions())
            }
        }
    }

    /**
     * The progression for the number of points earned per tick.
     */
    private fun ObjectiveOwnerCount?.pptProgression() = progression(
        data = this?.pointsPerTick,
        title = Gw2Resources.strings.points_per_tick.desc(),

        // The PPT icon is just a smaller war score icon (16x16 compared to 32x32).
        // Normally the war score icon would be tinted to match the team color though.
        icon = Gw2Resources.images.war_score.asImageDesc()
    )

    /**
     * The progression for the number of victory points earned for the entire match.
     */
    private fun WvwMatchObjectiveOwnerCount?.vpProgression() = progression(
        data = this?.victoryPoints,
        title = Gw2Resources.strings.victory_points.desc(),
        icon = Gw2Resources.images.victory_points.asImageDesc()
    )

    /**
     * The progression for the total score earned for the entire match.
     */
    private fun ObjectiveOwnerCount?.scoreProgression() = progression(
        data = this?.scores,
        title = Gw2Resources.strings.war_score.desc(),
        icon = Gw2Resources.images.war_score.asImageDesc()
    )

    /**
     * The progression for the total number of kills earned for the entire match.
     */
    private fun ObjectiveOwnerCount?.killProgression() = progression(
        data = this?.kills,
        title = Gw2Resources.strings.kills.desc(),
        icon = Gw2Resources.images.enemy_dead.asImageDesc()
    )

    /**
     * The progression for the total number of deaths given the entire match.
     */
    private fun ObjectiveOwnerCount?.deathProgression() = progression(
        data = this?.deaths,
        title = Gw2Resources.strings.deaths.desc(),
        icon = Gw2Resources.images.ally_dead.asImageDesc()
    )

    private fun progression(data: Map<out WvwObjectiveOwner?, Int>?, title: StringDesc, icon: ImageDesc): Progression {
        val total = data.total().toFloat()
        return Progression(
            title = title,
            icon = icon,
            progress = owners.map { owner ->
                val amount = data?.get(owner) ?: 0
                Progress(
                    color = owner.color(),
                    amount = amount,
                    owner = owner.stringDesc(),
                    percentage = when {
                        total <= 0 -> 1f / owners.size
                        else -> amount / total
                    }
                )
            }
        )
    }

    /**
     * The progressions for the count of each of the [objectiveTypes].
     */
    private fun ObjectiveOwnerCount?.contestedAreaProgressions(): List<Progression> = buildList {
        val areas = this@contestedAreaProgressions?.contestedAreas?.byType ?: return@buildList
        areas.filter(objectiveTypes, owners).forEach { counts ->
            val total = counts.sumOf { count -> count.size }.toFloat()

            // Stonemist Castle only exists in Eternal Battlegrounds so it shouldn't be displayed in the other borderlands.
            // Also, if we are pending objectives then don't bother showing the progression until we have at least 1 to showcase.
            if (total <= 0) {
                return@forEach
            }

            val type = counts.type
            val objectives = repositories.selectedWorld.objectives
            val link = objectives[counts.sample?.id]?.iconLink
            Progression(
                title = type.stringDesc(),
                icon = link?.value?.asImageUrl(),
                progress = counts.map { count ->
                    val amount = count.size
                    val owner = count.owner
                    Progress(
                        color = owner.color(),
                        amount = amount,
                        owner = owner.stringDesc(),
                        percentage = when {
                            total <= 0 -> 1f / counts.size
                            else -> amount / total
                        }
                    )
                }
            ).addTo(this)
        }
    }
}