package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.common.ImageImpl
import com.bselzer.gw2.manager.common.ui.layout.custom.progression.model.Progress
import com.bselzer.gw2.manager.common.ui.layout.custom.progression.model.Progression
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.extension.decodeOrNull
import com.bselzer.gw2.v2.model.extension.wvw.count.ObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.count.ObjectiveOwnerScore
import com.bselzer.gw2.v2.model.extension.wvw.count.WvwMatchObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.count.contestedarea.ContestedAreasCountByType
import com.bselzer.gw2.v2.model.extension.wvw.objectiveOwnerCount
import com.bselzer.gw2.v2.model.wvw.map.WvwMap
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.gw2.v2.resource.strings.stringDesc
import com.bselzer.ktx.function.collection.addTo
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.asImageDesc
import dev.icerock.moko.resources.desc.image.asImageUrl

class WvwMatchStatisticsViewModel(
    context: AppComponentContext,
    showDialog: (DialogConfig) -> Unit
) : WvwMatchBorderlandsViewModel<List<Progression>>(context, showDialog) {
    override val title: StringDesc = KtxResources.strings.statistics.desc()

    override val defaultData: List<Progression> = emptyList()

    override fun overviewData(): List<Progression> = with(count) {
        buildList {
            add(vpProgression())
            add(pptProgression())
            add(lastSkirmish.skirmishScoreProgression())
            add(totalScoreProgression())
            add(killProgression())
            add(deathProgression())
            addAll(contestedAreaProgressions())
        }
    }

    override fun borderlandData(map: WvwMap): List<Progression> = with(
        map.objectiveOwnerCount()
    ) {
        buildList {
            add(pptProgression())

            val mapScore = object : ObjectiveOwnerScore {
                override val scores: Map<WvwObjectiveOwner, Int> = lastSkirmish.mapScores[map.type.decodeOrNull()] ?: emptyMap()
            }
            add(mapScore.skirmishScoreProgression())

            add(totalScoreProgression())
            add(killProgression())
            add(deathProgression())
            addAll(contestedAreaProgressions())
        }
    }

    /**
     * The progression for the number of points earned per tick.
     */
    private fun ObjectiveOwnerCount?.pptProgression() = this?.pointsPerTick.progression(
        title = Gw2Resources.strings.points_per_tick.desc(),

        // The PPT icon is just a smaller war score icon (16x16 compared to 32x32).
        image = Gw2Resources.images.war_score.asImageDesc()
    )

    /**
     * The progression for the number of victory points earned for the entire match.
     */
    private fun WvwMatchObjectiveOwnerCount?.vpProgression() = this?.victoryPoints.progression(
        title = Gw2Resources.strings.victory_points.desc(),
        image = Gw2Resources.images.victory_points.asImageDesc()
    )

    private fun ObjectiveOwnerScore?.scoreProgression(
        title: StringDesc
    ): Progression {
        val winner = this?.scores?.maxByOrNull { score -> score.value }?.key
        return this?.scores.progression(
            title = title,
            image = if (winner == null) Gw2Resources.images.war_score.asImageDesc() else configuration.wvw.icons.warScore.asImageUrl(),

            // Tint to help distinguish between this and the PPT icon which is inherently the same.
            color = winner?.color()
        )
    }

    /**
     * The progression for the total score earned for the entire match.
     */
    private fun ObjectiveOwnerScore?.skirmishScoreProgression(): Progression = scoreProgression(Gw2Resources.strings.skirmish_score.desc())

    /**
     * The progression for the total score earned for the entire match.
     */
    private fun ObjectiveOwnerCount?.totalScoreProgression(): Progression = scoreProgression(Gw2Resources.strings.total_score.desc())

    /**
     * The progression for the total number of kills earned for the entire match.
     */
    private fun ObjectiveOwnerCount?.killProgression() = this?.kills.progression(
        title = Gw2Resources.strings.kills.desc(),
        image = Gw2Resources.images.enemy_dead.asImageDesc()
    )

    /**
     * The progression for the total number of deaths given the entire match.
     */
    private fun ObjectiveOwnerCount?.deathProgression() = this?.deaths.progression(
        title = Gw2Resources.strings.deaths.desc(),
        image = Gw2Resources.images.ally_dead.asImageDesc()
    )

    private fun Map<out WvwObjectiveOwner?, Int>?.progression(
        title: StringDesc,
        image: ImageDesc,
        color: Color? = null
    ): Progression {
        val total = total().toFloat()
        return Progression(
            title = title,
            image = ImageImpl(image = image, color = color),
            progress = owners.map { owner ->
                val amount = this?.get(owner) ?: 0
                owner.progress(amount, total, owners.size)
            }
        )
    }

    /**
     * Represents the owner's progress as a ratio of the [amount] to the [total].
     * If the total is <= 0, then progress is evenly split amongst the [count] owners.
     */
    private fun WvwObjectiveOwner.progress(amount: Int, total: Float, count: Int) = Progress(
        color = color(),
        amount = amount,
        owner = stringDesc(),
        percentage = when {
            total <= 0 -> 1f / count
            else -> amount / total
        }
    )


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

            counts.progression(total).addTo(this)
        }
    }

    private fun ContestedAreasCountByType.progression(total: Float): Progression {
        val objectives = repositories.selectedWorld.objectives
        val link = objectives[sample?.id]?.iconLink
        return Progression(
            title = type.stringDesc(),
            image = ImageImpl(
                image = link?.value?.asImageUrl(),

                // Tint with the winner.
                color = maxByOrNull { count -> count.size }?.owner.color()
            ),
            progress = map { count ->
                val amount = count.size
                val owner = count.owner
                owner.progress(amount, total, size)
            }
        )
    }
}