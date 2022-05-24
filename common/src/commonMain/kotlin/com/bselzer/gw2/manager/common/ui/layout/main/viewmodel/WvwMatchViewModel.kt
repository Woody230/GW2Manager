package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.configuration.WvwHelper.color
import com.bselzer.gw2.manager.common.configuration.WvwHelper.displayableLinkedWorlds
import com.bselzer.gw2.manager.common.configuration.WvwHelper.stringResource
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.Chart
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.ChartData
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.ChartSlice
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.Charts
import com.bselzer.gw2.v2.model.enumeration.WvwMapType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.extension.enumValueOrNull
import com.bselzer.gw2.v2.model.extension.wvw.*
import com.bselzer.ktx.function.collection.addTo
import com.bselzer.ktx.function.objects.userFriendly
import com.bselzer.ktx.resource.Resources
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.asImageUrl
import dev.icerock.moko.resources.format

class WvwMatchViewModel(context: AppComponentContext) : MainViewModel(context) {
    override val title: StringDesc = Gw2Resources.strings.wvw_match.desc()

    // TODO actions

    /**
     * The team color of the owner to create charts for.
     */
    private val owners: Collection<WvwObjectiveOwner> = listOf(WvwObjectiveOwner.BLUE, WvwObjectiveOwner.GREEN, WvwObjectiveOwner.RED)

    /**
     * The maps to create charts for.
     */
    private val maps: Collection<WvwMapType> = listOf(WvwMapType.ETERNAL_BATTLEGROUNDS, WvwMapType.BLUE_BORDERLANDS, WvwMapType.GREEN_BORDERLANDS, WvwMapType.RED_BORDERLANDS)

    /**
     * The default charts to use when attempting to index into the [charts].
     */
    val defaultCharts = Charts(
        title = StringDesc.Raw(""),
        color = configuration.wvw.color(WvwObjectiveOwner.NEUTRAL),
        charts = emptyList()
    )

    /**
     * All the charts: an overview of the match and for each individual map
     */
    val charts: Collection<Charts>
        @Composable
        get() = run {
            // Maintain a consistent map order.
            val charts = borderlandCharts.entries.sortedBy { entry -> maps.indexOf(entry.key) }.toMutableList()

            // Add the total charts first as the match overview.
            charts.add(0, object : Map.Entry<WvwMapType?, List<Chart>> {
                override val key: WvwMapType? = null
                override val value: List<Chart> = overviewCharts
            })

            charts.map { entry ->
                // TODO get translated from map or continent
                val title = entry.key?.userFriendly()
                Charts(
                    // Use the map type as the title, otherwise default to the match overview for the null type that was added.
                    title = if (!title.isNullOrBlank()) StringDesc.Raw(title) else Resources.strings.overview.desc(),
                    color = configuration.wvw.color(entry.key?.owner()),
                    charts = entry.value
                )
            }
        }

    /**
     * The charts associated with the match total.
     */
    private val overviewCharts
        @Composable
        get() = run {
            val match = repositories.wvw.selectedMatch().collectAsState(null).value
            listOf(
                match?.victoryPoints().vpChart(),
                match?.pointsPerTick().pptChart(),
                match?.scores().scoreChart(),
                match?.kills().killChart(),
                match?.deaths().deathChart()
            )
        }

    /**
     * The charts associated with each individual map.
     */
    private val borderlandCharts
        @Composable
        get() = run {
            val match = repositories.wvw.selectedMatch().collectAsState(null).value
            val maps = match?.maps ?: emptyList()
            maps.associateBy { map -> map.type.enumValueOrNull() }.mapValues { entry ->
                val map = entry.value
                listOf(
                    map.pointsPerTick().pptChart(),
                    map.scores().scoreChart(),
                    map.kills().killChart(),
                    map.deaths().deathChart()
                )
            }
        }

    /**
     * The chart for the number of points earned per tick.
     */
    @Composable
    private fun Map<out WvwObjectiveOwner?, Int>?.pptChart() = chart(this, Gw2Resources.strings.points_per_tick.desc())

    /**
     * The chart for the number of victory points earned for the entire match.
     */
    @Composable
    private fun Map<out WvwObjectiveOwner?, Int>?.vpChart() = chart(this, Gw2Resources.strings.victory_points.desc())

    /**
     * The chart for the total score earned for the entire match.
     */
    @Composable
    private fun Map<out WvwObjectiveOwner?, Int>?.scoreChart() = chart(this, Gw2Resources.strings.total_score.desc())

    /**
     * The chart for the total number of kills earned for the entire match.
     */
    @Composable
    private fun Map<out WvwObjectiveOwner?, Int>?.killChart() = chart(this, Gw2Resources.strings.total_kills.desc())

    /**
     * The chart for the total number of deaths given the entire match.
     */
    @Composable
    private fun Map<out WvwObjectiveOwner?, Int>?.deathChart() = chart(this, Gw2Resources.strings.total_deaths.desc())

    @Composable
    private fun chart(data: Map<out WvwObjectiveOwner?, Int>?, title: StringDesc): Chart = Chart(
        title = title,
        data = datas(data),
        background = configuration.wvw.chart.backgroundLink.asImageUrl(),
        divider = configuration.wvw.chart.dividerLink.asImageUrl(),
        slices = slices(data)
    )

    @Composable
    private fun datas(data: Map<out WvwObjectiveOwner?, Int>?): Collection<ChartData> = buildList {
        val worlds = repositories.world.worlds().collectAsState(emptyList()).value
        val match = repositories.wvw.selectedMatch().collectAsState(null).value
        owners.forEach { owner ->
            val amount = data?.get(owner) ?: 0
            ChartData(
                color = configuration.wvw.color(owner),
                data = StringDesc.Raw(amount.toString()),

                // TODO translated world names
                owner = StringDesc.Raw(worlds.displayableLinkedWorlds(match, owner))
            ).addTo(this)
        }
    }

    /**
     * Creates a neutral slice and a slice for each of the [owners] using the proportioned amount defined by the [data].
     */
    private fun slices(data: Map<out WvwObjectiveOwner?, Int>?): Collection<ChartSlice> = buildList {
        // Add the neutral slice first to act as a background behind the owned slices.
        ChartSlice(
            description = Gw2Resources.strings.neutral_slice.desc(),
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
                description = Gw2Resources.strings.owned_slice.format(angle, owner.stringResource()),
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