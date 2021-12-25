package com.bselzer.gw2.manager.android.wvw.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.bselzer.gw2.manager.android.wvw.state.match.ChartState
import com.bselzer.gw2.manager.android.wvw.state.match.WvwMatchState
import com.bselzer.gw2.manager.android.wvw.state.match.description.ChartDataState
import com.bselzer.gw2.manager.android.wvw.state.match.description.ChartDescriptionState
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.gw2.manager.common.ui.composable.ImageContent
import com.bselzer.ktx.compose.ui.container.DividedColumn
import com.bselzer.ktx.compose.ui.geometry.ArcShape

class WvwMatchPage(
    aware: Gw2Aware,
    navigateUp: () -> Unit,
    appBarActions: @Composable RowScope.() -> Unit,
    state: WvwMatchState,
) : WvwContentPage<WvwMatchState>(aware, navigateUp, appBarActions, state) {
    @Composable
    override fun Content() = Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar()

        // TODO pager: main = total, then for each map (will need map name title on each page)
        AbsoluteBackground(modifier = Modifier.fillMaxSize()) {
            DividedColumn(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                divider = { Spacer(modifier = Modifier.height(5.dp)) },
                contents = state.charts.value.map { chart -> pieChart(chart) }.toTypedArray()
            )
        }
    }

    @Composable
    override fun topAppBarTitle(): String = stringResource(id = R.string.wvw_match)

    /**
     * Lays out a pie chart.
     */
    @Composable
    private fun pieChart(chart: ChartState): @Composable ColumnScope.() -> Unit = {
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