package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.repository.instance.generic.ColorData
import com.bselzer.gw2.manager.common.repository.instance.specialized.SelectedWorldData
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.AppBarAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.SelectedWorldRefreshAction.Companion.refreshAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.WorldSelectionAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.Chart
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.ChartData
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.ChartSlice
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
import com.bselzer.ktx.function.collection.addTo
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.asImageUrl
import dev.icerock.moko.resources.format

class WvwMatchViewModel(
    context: AppComponentContext,
    private val showDialog: (DialogConfig) -> Unit
) : MainViewModel(context), SelectedWorldData by context.repositories.selectedWorld, ColorData by context.repositories.color {
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
            charts.add(0, object : Map.Entry<WvwMapType?, List<Chart>> {
                override val key: WvwMapType? = null
                override val value: List<Chart> = overviewCharts
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
    private val overviewCharts: List<Chart>
        get() = match?.objectiveOwnerCount()?.run {
            listOf(vpChart(), pptChart(), scoreChart(), killChart(), deathChart())
        } ?: emptyList()

    /**
     * The charts associated with each individual map.
     */
    private val borderlandCharts: Map<WvwMapType?, List<Chart>>
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

    private fun chart(data: Map<out WvwObjectiveOwner?, Int>?, title: StringDesc): Chart = Chart(
        title = title,
        data = datas(data),
        background = configuration.wvw.chart.backgroundLink.asImageUrl(),
        divider = configuration.wvw.chart.dividerLink.asImageUrl(),
        slices = slices(data)
    )

    private fun datas(data: Map<out WvwObjectiveOwner?, Int>?): Collection<ChartData> = buildList {
        owners.forEach { owner ->
            val amount = data?.get(owner) ?: 0
            ChartData(
                color = owner.color(),
                data = amount.toString().desc(),
                owner = repositories.selectedWorld.displayableLinkedWorlds(owner)
            ).addTo(this)
        }
    }

    /**
     * Creates a neutral slice and a slice for each of the [owners] using the proportioned amount defined by the [data].
     */
    private fun slices(data: Map<out WvwObjectiveOwner?, Int>?): Collection<ChartSlice> = buildList {
        // Add the neutral slice first to act as a background behind the owned slices.
        ChartSlice(
            description = AppResources.strings.neutral_slice.desc(),
            startAngle = 0f,
            endAngle = 0f,
            image = configuration.wvw.chart.neutralLink.asImageUrl()
        ).addTo(this)

        // Add the owned slices.
        val total = data.total()
        var startAngle = 0f
        owners.forEach { owner ->
            val amount = data?.get(owner) ?: 0
            val angle = if (total <= 0) 120f else amount / total * 360f

            ChartSlice(
                description = AppResources.strings.owned_slice.format(angle, owner.stringDesc()),
                startAngle = startAngle,
                endAngle = startAngle + angle,
                image = when (owner) {
                    WvwObjectiveOwner.RED -> configuration.wvw.chart.redLink
                    WvwObjectiveOwner.BLUE -> configuration.wvw.chart.blueLink
                    WvwObjectiveOwner.GREEN -> configuration.wvw.chart.greenLink
                    WvwObjectiveOwner.NEUTRAL -> configuration.wvw.chart.neutralLink
                }.asImageUrl()
            ).addTo(this)

            // Set up the next slice.
            startAngle += angle
        }
    }

    /**
     * Gets the sum of all the values in the map whose owner exists in the collection of [owners] to create a chart for.
     */
    private fun Map<out WvwObjectiveOwner?, Int>?.total() = run {
        // Using float for total to avoid int division.
        this?.filterKeys { owner -> owners.contains(owner) }?.values?.sum()?.toFloat() ?: 0f
    }
}