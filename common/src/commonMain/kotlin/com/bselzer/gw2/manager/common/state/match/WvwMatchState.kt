package com.bselzer.gw2.manager.common.state.match

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.common.state.WvwHelper.color
import com.bselzer.gw2.manager.common.state.WvwHelper.displayableLinkedWorlds
import com.bselzer.gw2.manager.common.state.core.Gw2State
import com.bselzer.gw2.manager.common.state.match.description.ChartDataState
import com.bselzer.gw2.manager.common.state.match.description.ChartDescriptionState
import com.bselzer.gw2.manager.common.state.match.description.ChartTitleState
import com.bselzer.gw2.manager.common.state.match.pie.ChartBackgroundState
import com.bselzer.gw2.manager.common.state.match.pie.ChartDividerState
import com.bselzer.gw2.manager.common.state.match.pie.ChartSliceState
import com.bselzer.gw2.v2.model.enumeration.extension.wvw.type
import com.bselzer.gw2.v2.model.enumeration.wvw.MapType
import com.bselzer.gw2.v2.model.enumeration.wvw.ObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.wvw.ObjectiveOwner.*
import com.bselzer.gw2.v2.model.extension.wvw.*
import com.bselzer.ktx.function.objects.userFriendly

class WvwMatchState(
    state: Gw2State,
    private val owners: Collection<ObjectiveOwner> = listOf(BLUE, GREEN, RED)
) : Gw2State by state {
    /**
     * The state of all the charts associated with the match total.
     */
    val totalCharts: State<Collection<ChartState>> = derivedStateOf {
        listOf(
            worldMatch.value?.victoryPoints().vpChart(),
            worldMatch.value?.pointsPerTick().pptChart(),
            worldMatch.value?.scores().scoreChart(),
            worldMatch.value?.kills().killChart(),
            worldMatch.value?.deaths().deathChart()
        )
    }

    /**
     * The state of all the charts for each individual map.
     */
    val borderlandCharts: State<Map<MapType?, Collection<ChartState>>> = derivedStateOf {
        val maps = worldMatch.value?.maps ?: emptyList()
        maps.associateBy { map -> map.type() }.mapValues { entry ->
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
     * The state of the chart for the number of points earned per tick.
     */
    private fun Map<out ObjectiveOwner?, Int>?.pptChart() = chart(data = this, title = "Points Per Tick")

    /**
     * The state of the chart for the number of victory points for the entire match.
     */
    private fun Map<out ObjectiveOwner?, Int>?.vpChart() = chart(data = this, title = "Victory Points")

    /**
     * The state of the chart for the total score count for the entire match.
     */
    private fun Map<out ObjectiveOwner?, Int>?.scoreChart() = chart(data = this, title = "Total Score")

    /**
     * The state of the chart for the total number of kills for the entire match.
     */
    private fun Map<out ObjectiveOwner?, Int>?.killChart() = chart(data = this, title = "Total Kills")

    /**
     * The state of the chart for the total number of deaths for the entire match.
     */
    private fun Map<out ObjectiveOwner?, Int>?.deathChart() = chart(data = this, title = "Total Deaths")

    /**
     * Creates a chart associated with the [data] for each of the [owners].
     */
    private fun chart(data: Map<out ObjectiveOwner?, Int>?, title: String): ChartState {
        // Using float for total to avoid int division.
        val total = data?.filterKeys { owner -> owners.contains(owner) }?.values?.sum()?.toFloat() ?: 0f

        val width = configuration.wvw.chart.size.width
        val height = configuration.wvw.chart.size.height

        val descriptions = mutableListOf<ChartDataState>()
        val slices = mutableListOf<ChartSliceState>().apply {
            // Add the neutral slice first.
            add(
                ChartSliceState(
                    width = width,
                    height = height,
                    description = "Neutral Slice",
                    startAngle = 0f,
                    endAngle = 360f,
                    link = configuration.wvw.chart.neutralLink
                )
            )
        }

        var startAngle = 0f
        for (owner in owners) {
            val amount = data?.get(owner) ?: 0
            val angle = if (total <= 0) 120f else amount / total * 360f
            slices.add(
                ChartSliceState(
                    width = width,
                    height = height,
                    description = "${owner.userFriendly()} $angle Degree Slice",
                    startAngle = startAngle,
                    endAngle = startAngle + angle,
                    link = when (owner) {
                        RED -> configuration.wvw.chart.redLink
                        BLUE -> configuration.wvw.chart.blueLink
                        GREEN -> configuration.wvw.chart.greenLink
                        NEUTRAL -> configuration.wvw.chart.neutralLink
                    }
                )
            )

            descriptions.add(
                ChartDataState(
                    // Get the names of the worlds associated with the owner.
                    owner = worlds.values.displayableLinkedWorlds(match = worldMatch.value, owner = owner),
                    data = amount.toString(),
                    color = configuration.wvw.color(owner = owner),
                    textSize = configuration.wvw.chart.data.textSize.sp
                )
            )

            // Set up the next slice.
            startAngle += angle
        }

        return ChartState(
            description = ChartDescriptionState(
                title = ChartTitleState(
                    title = title,
                    size = configuration.wvw.chart.title.textSize.sp
                ),
                data = descriptions
            ),
            divider = ChartDividerState(
                link = configuration.wvw.chart.dividerLink,
                width = width,
                height = height,
            ),
            background = ChartBackgroundState(
                link = configuration.wvw.chart.backgroundLink,
                width = width,
                height = height
            ),
            slices = slices,
        )
    }
}