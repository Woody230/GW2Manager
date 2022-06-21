package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import com.bselzer.gw2.manager.common.repository.data.specific.SelectedWorldData
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.chart.viewmodel.ChartViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.AppBarAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.SelectedWorldRefreshAction.Companion.refreshAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.WorldSelectionAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.Charts
import com.bselzer.gw2.v2.model.enumeration.WvwMapType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.extension.enumValueOrNull
import com.bselzer.gw2.v2.model.extension.wvw.ObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.WvwMatchObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.objectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.owner
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.gw2.v2.resource.strings.stringDesc
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
     * The default charts to use when attempting to index into the [charts].
     */
    val defaultCharts = Charts(
        title = "".desc(),
        color = WvwObjectiveOwner.NEUTRAL.color(),
        charts = emptyList()
    )

    /**
     * All the charts: an overview of the match and for each individual map
     */
    val charts: Collection<Charts>
        get() = run {
            // Maintain a consistent map order.
            val charts = borderlandCharts.entries.sortedBy { entry -> mapTypes.indexOf(entry.key) }.toMutableList()

            // Add the total charts first as the match overview.
            charts.add(0, object : Map.Entry<WvwMapType?, List<ChartViewModel>> {
                override val key: WvwMapType? = null
                override val value: List<ChartViewModel> = overviewCharts
            })

            charts.map { entry ->
                Charts(
                    // Use the map type as the title, otherwise default to the match overview for the null type that was added.
                    title = entry.key?.stringDesc() ?: KtxResources.strings.overview.desc(),
                    color = entry.key?.owner().color(),
                    charts = entry.value
                )
            }
        }

    /**
     * The charts associated with the match total.
     */
    private val overviewCharts: List<ChartViewModel>
        get() = match?.objectiveOwnerCount()?.run {
            listOf(vpChart(), pptChart(), scoreChart(), killChart(), deathChart())
        } ?: emptyList()

    /**
     * The charts associated with each individual map.
     */
    private val borderlandCharts: Map<WvwMapType?, List<ChartViewModel>>
        get() = run {
            val maps = match?.maps ?: emptyList()
            maps.associateBy { map -> map.type.enumValueOrNull() }.mapValues { entry ->
                with(entry.value.objectiveOwnerCount()) {
                    listOf(pptChart(), scoreChart(), killChart(), deathChart())
                }
            }
        }

    /**
     * The chart for the number of points earned per tick.
     */
    private fun ObjectiveOwnerCount?.pptChart() = chart(this?.pointsPerTick, Gw2Resources.strings.points_per_tick.desc())

    /**
     * The chart for the number of victory points earned for the entire match.
     */
    private fun WvwMatchObjectiveOwnerCount?.vpChart() = chart(this?.victoryPoints, Gw2Resources.strings.victory_points.desc())

    /**
     * The chart for the total score earned for the entire match.
     */
    private fun ObjectiveOwnerCount?.scoreChart() = chart(this?.scores, Gw2Resources.strings.total_score.desc())

    /**
     * The chart for the total number of kills earned for the entire match.
     */
    private fun ObjectiveOwnerCount?.killChart() = chart(this?.kills, Gw2Resources.strings.total_kills.desc())

    /**
     * The chart for the total number of deaths given the entire match.
     */
    private fun ObjectiveOwnerCount?.deathChart() = chart(this?.deaths, Gw2Resources.strings.total_deaths.desc())

    private fun chart(data: Map<out WvwObjectiveOwner?, Int>?, title: StringDesc): ChartViewModel = ChartViewModel(
        context = this,
        data = data,
        title = title
    )
}