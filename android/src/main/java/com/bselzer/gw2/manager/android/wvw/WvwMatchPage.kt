package com.bselzer.gw2.manager.android.wvw

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.common.BackgroundType
import com.bselzer.gw2.manager.common.state.core.Gw2State
import com.bselzer.gw2.manager.common.state.match.ChartState
import com.bselzer.gw2.manager.common.state.match.WvwMatchState
import com.bselzer.gw2.manager.common.state.match.description.ChartDataState
import com.bselzer.gw2.manager.common.state.match.description.ChartDescriptionState
import com.bselzer.gw2.manager.common.ui.composable.ImageContent
import com.bselzer.ktx.compose.ui.geometry.ArcShape
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager

class WvwMatchPage(
    navigationIcon: @Composable () -> Unit,
    state: WvwMatchState,
) : WvwPage<WvwMatchState>(navigationIcon, state) {
    @Composable
    override fun background() = BackgroundType.ABSOLUTE

    // TODO vertical paging for each map (will need map name title on each page)
    @OptIn(ExperimentalPagerApi::class)
    @Composable
    override fun Gw2State.CoreContent() {
        val charts = state.charts.value.toList()
        HorizontalPager(
            count = charts.size,
            contentPadding = PaddingValues(vertical = 25.dp),
            modifier = Modifier.fillMaxSize()
        ) { index ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                PieChart(chart = charts[index])
            }
        }
    }

    @Composable
    override fun title(): String = stringResource(id = R.string.wvw_match)

    /**
     * Lays out a pie chart with the data describing it.
     */
    @Composable
    private fun PieChart(chart: ChartState) {
        Box {
            chart.background.ImageContent()
            chart.slices.forEach { slice ->
                slice.ImageContent(
                    modifier = Modifier.clip(ArcShape(slice.startAngle, slice.endAngle))
                )
            }

            // Add the dividers between the slices.
            chart.slices.map { slice -> slice.startAngle }.forEach { angle ->
                chart.divider.ImageContent(modifier = Modifier.rotate(degrees = angle))
            }
        }

        ChartDescription(description = chart.description)
    }

    /**
     * Lays out a description of the chart with its associated data.
     */
    @Composable
    private fun ChartDescription(description: ChartDescriptionState) = RelativeBackgroundColumn(
        modifier = Modifier.fillMaxWidth()
    )
    {
        Text(text = description.title.title, fontWeight = FontWeight.Bold, fontSize = description.title.size, textAlign = TextAlign.Center)

        // Show the data representing each slice.
        description.data.forEach { data -> ChartData(data) }
    }

    /**
     * Lays out the data associated with a slice.
     */
    @Composable
    private fun ChartData(data: ChartDataState) {
        Text(
            text = data.owner,
            fontWeight = FontWeight.Bold,
            fontSize = data.textSize,
            color = data.color,
            textAlign = TextAlign.Center
        )
        Text(text = data.data, fontSize = data.textSize, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(3.dp))
    }
}