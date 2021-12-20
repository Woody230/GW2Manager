package com.bselzer.gw2.manager.android.ui.activity.wvw.state.match

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.android.ui.activity.wvw.WvwHelper.displayableLinkedWorlds
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.match.description.ChartDataState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.match.description.ChartDescriptionState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.match.description.ChartTitleState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.match.pie.ChartBackgroundState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.match.pie.ChartDividerState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.match.pie.ChartSliceState
import com.bselzer.gw2.manager.common.configuration.wvw.Wvw
import com.bselzer.gw2.v2.model.enumeration.wvw.ObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.wvw.ObjectiveOwner.*
import com.bselzer.gw2.v2.model.extension.wvw.*
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.ktx.function.objects.userFriendly

data class WvwMatchState(
    private val configuration: Wvw,
    private val match: State<WvwMatch?>,
    private val worlds: State<Collection<World>>,
    private val owners: Collection<ObjectiveOwner>
) {
    /**
     * The state of the chart for the number of points earned per tick.
     */
    private val pointsPerTick: State<ChartState> = derivedStateOf {
        chart(data = match.value?.pointsPerTick(), title = "Points Per Tick")
    }

    /**
     * The state of the chart for the total number of victory points for the entire match.
     */
    private val victoryPoints: State<ChartState> = derivedStateOf {
        chart(data = match.value?.victoryPoints(), title = "Victory Points")
    }

    /**
     * The state of the chart for the total score count for the entire match.
     */
    private val scores: State<ChartState> = derivedStateOf {
        chart(data = match.value?.scores(), "Total Score")
    }

    /**
     * The state of the chart for the total number of kills for the entire match.
     */
    private val kills: State<ChartState> = derivedStateOf {
        chart(data = match.value?.kills(), "Total Kills")
    }

    /**
     * The state of the chart for the total number of deaths for the entire match.
     */
    private val deaths: State<ChartState> = derivedStateOf {
        chart(data = match.value?.deaths(), "Total Deaths")
    }

    /**
     * The state of all the charts associated with the match.
     */
    val charts: State<Collection<ChartState>> = derivedStateOf {
        listOf(pointsPerTick.value, victoryPoints.value, scores.value, kills.value, deaths.value)
    }

    /**
     * Creates a chart associated with the [data] for each of the [owners].
     */
    private fun chart(data: Map<out ObjectiveOwner?, Int>?, title: String): ChartState {
        // Using float for total to avoid int division.
        val total = data?.filterKeys { owner -> owners.contains(owner) }?.values?.sum()?.toFloat() ?: 0f

        val width = configuration.chart.size.width
        val height = configuration.chart.size.height

        val descriptions = mutableListOf<ChartDataState>()
        val slices = mutableListOf<ChartSliceState>()
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
                        RED -> configuration.chart.redLink
                        BLUE -> configuration.chart.blueLink
                        GREEN -> configuration.chart.greenLink
                        NEUTRAL -> configuration.chart.neutralLink
                    }
                )
            )

            descriptions.add(
                ChartDataState(
                    // Get the names of the worlds associated with the owner.
                    owner = worlds.value.displayableLinkedWorlds(match = match.value, owner = owner),
                    data = amount.toString(),
                    color = configuration.objectives.color(owner = owner),
                    textSize = configuration.chart.data.textSize.sp
                )
            )

            // Set up the next slice.
            startAngle += angle
        }

        return ChartState(
            description = ChartDescriptionState(
                title = ChartTitleState(
                    title = title,
                    size = configuration.chart.title.textSize.sp
                ),
                data = descriptions
            ),
            divider = ChartDividerState(
                link = configuration.chart.dividerLink,
                width = width,
                height = height,
            ),
            background = ChartBackgroundState(
                shadowLink = configuration.chart.backgroundLink,
                neutralLink = configuration.chart.neutralLink,
                width = width,
                height = height
            ),
            slices = slices,
        )
    }
}