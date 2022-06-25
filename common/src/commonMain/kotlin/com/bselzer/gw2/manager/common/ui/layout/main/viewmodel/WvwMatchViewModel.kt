package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import com.bselzer.gw2.manager.common.repository.data.specialized.SelectedWorldData
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.AppBarAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.SelectedWorldRefreshAction.Companion.refreshAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.WorldSelectionAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.DataSet
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.Progress
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.Progression
import com.bselzer.gw2.v2.model.enumeration.WvwMapType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.extension.enumValueOrNull
import com.bselzer.gw2.v2.model.extension.wvw.count.ObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.count.WvwMatchObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.objectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.owner
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.gw2.v2.resource.strings.stringDesc
import com.bselzer.ktx.function.collection.addTo
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class WvwMatchViewModel(
    context: AppComponentContext,
    private val showDialog: (DialogConfig) -> Unit
) : MainViewModel(context), SelectedWorldData by context.repositories.selectedWorld {
    override val title: StringDesc = Gw2Resources.strings.match.desc()

    override val actions: List<AppBarAction>
        get() = listOf(
            refreshAction(),
            WorldSelectionAction(showDialog)
        )

    /**
     * The default information to use when attempting to index into the [datasets].
     */
    val defaultData = DataSet(
        title = "".desc(),
        color = WvwObjectiveOwner.NEUTRAL.color(),
        progressions = emptyList()
    )

    /**
     * All the progressions: an overview of the match and for each individual map
     */
    val datasets: List<DataSet>
        get() = run {
            // Maintain a consistent map order.
            val progressions = borderlandProgressions.entries.sortedBy { entry -> mapTypes.indexOf(entry.key) }.toMutableList()

            // Add the total progressions first as the match overview.
            progressions.add(0, object : Map.Entry<WvwMapType?, List<Progression>> {
                override val key: WvwMapType? = null
                override val value: List<Progression> = overviewProgressions
            })

            progressions.map { entry ->
                DataSet(
                    // TODO use linked worlds instead for title? or just main world?
                    // Use the map type as the title, otherwise default to the match overview for the null type that was added.
                    title = entry.key?.stringDesc() ?: KtxResources.strings.overview.desc(),
                    color = entry.key?.owner().color(),
                    progressions = entry.value
                )
            }
        }

    /**
     * The progressions associated with the match total.
     */
    private val overviewProgressions: List<Progression>
        get() {
            val count = match?.objectiveOwnerCount() ?: return emptyList()
            return buildList {
                with(count) {
                    add(vpProgression())
                    add(pptProgression())
                    add(scoreProgression())
                    add(killProgression())
                    add(deathProgression())
                    addAll(contestedAreaProgressions())
                }
            }
        }

    /**
     * The progressions associated with each individual map.
     */
    private val borderlandProgressions: Map<WvwMapType?, List<Progression>>
        get() = run {
            val maps = match?.maps ?: emptyList()
            maps.associateBy { map -> map.type.enumValueOrNull() }.mapValues { (type, map) ->
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
        }

    /**
     * The progression for the number of points earned per tick.
     */
    private fun ObjectiveOwnerCount?.pptProgression() = progression(this?.pointsPerTick, Gw2Resources.strings.points_per_tick.desc())

    /**
     * The progression for the number of victory points earned for the entire match.
     */
    private fun WvwMatchObjectiveOwnerCount?.vpProgression() = progression(this?.victoryPoints, Gw2Resources.strings.victory_points.desc())

    /**
     * The progression for the total score earned for the entire match.
     */
    private fun ObjectiveOwnerCount?.scoreProgression() = progression(this?.scores, Gw2Resources.strings.total_score.desc())

    /**
     * The progression for the total number of kills earned for the entire match.
     */
    private fun ObjectiveOwnerCount?.killProgression() = progression(this?.kills, Gw2Resources.strings.total_kills.desc())

    /**
     * The progression for the total number of deaths given the entire match.
     */
    private fun ObjectiveOwnerCount?.deathProgression() = progression(this?.deaths, Gw2Resources.strings.total_deaths.desc())

    /**
     * The progressions for the count of each of the [objectiveTypes].
     */
    private fun ObjectiveOwnerCount?.contestedAreaProgressions(): List<Progression> = buildList {
        val areas = this@contestedAreaProgressions?.contestedAreas?.byType ?: return@buildList
        areas.filter(objectiveTypes, owners).forEach { counts ->
            val total = counts.sumOf { count -> count.objectives }.toFloat()

            // Stonemist Castle only exists in Eternal Battlegrounds so it shouldn't be displayed in the other borderlands.
            // Also, if we are pending objectives then don't bother showing the progression until we have at least 1 to showcase.
            if (total <= 0) {
                return@forEach
            }

            Progression(
                title = counts.type.stringDesc(),
                progress = counts.map { count ->
                    val amount = count.objectives
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

    private fun progression(data: Map<out WvwObjectiveOwner?, Int>?, title: StringDesc): Progression {
        val total = data.total().toFloat()
        return Progression(
            title = title,
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
}