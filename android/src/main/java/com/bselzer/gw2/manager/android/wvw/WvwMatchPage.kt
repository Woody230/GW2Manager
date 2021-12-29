package com.bselzer.gw2.manager.android.wvw

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.common.BackgroundType
import com.bselzer.gw2.manager.common.state.WvwHelper.color
import com.bselzer.gw2.manager.common.state.core.Gw2State
import com.bselzer.gw2.manager.common.state.match.ChartState
import com.bselzer.gw2.manager.common.state.match.ChartsState
import com.bselzer.gw2.manager.common.state.match.WvwMatchState
import com.bselzer.gw2.manager.common.state.match.description.ChartDataState
import com.bselzer.gw2.manager.common.state.match.description.ChartDescriptionState
import com.bselzer.gw2.manager.common.ui.composable.ImageContent
import com.bselzer.gw2.manager.common.ui.theme.Purple200
import com.bselzer.gw2.v2.model.enumeration.wvw.ObjectiveOwner
import com.bselzer.ktx.compose.ui.geometry.ArcShape
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

class WvwMatchPage(
    navigationIcon: @Composable () -> Unit,
    state: WvwMatchState,
) : WvwPage<WvwMatchState>(navigationIcon, state) {
    @Composable
    override fun background() = BackgroundType.ABSOLUTE

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    override fun Gw2State.CoreContent() = ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        val (tabs, pager, indicators) = createRefs()
        val allCharts = state.charts.value

        // Lay out the tabs representing each map.
        var selectedIndex by remember { mutableStateOf(0) }
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            indicator = { },
            modifier = Modifier.constrainAs(tabs) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            // TODO since overview has the extra chart for victory points, feels awkward to swap between overview and a borderland and get a different type of chart
            allCharts.forEachIndexed { index, charts ->
                Tab(
                    text = { Text(text = charts.title) },
                    selected = index == selectedIndex,
                    onClick = { selectedIndex = index }
                )
            }
        }

        // Lay out the charts for the currently selected map.
        val pagerState = rememberPagerState()
        val selectedCharts = allCharts.getOrElse(selectedIndex) { ChartsState(title = "", color = configuration.wvw.color(ObjectiveOwner.NEUTRAL), charts = emptyList()) }
        HorizontalPager(
            count = selectedCharts.charts.size,
            state = pagerState,
            modifier = Modifier.constrainAs(pager) {
                top.linkTo(tabs.bottom, margin = 25.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(indicators.top, margin = 10.dp)
                height = Dimension.fillToConstraints
            }
        ) { index ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                PieChart(chart = selectedCharts.charts[index])
            }
        }

        // Lay out the indicators representing each chart for the selected map.
        HorizontalPagerIndicator(
            pagerState = pagerState,
            inactiveColor = Purple200,
            activeColor = selectedCharts.color,
            modifier = Modifier.constrainAs(indicators) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, margin = 25.dp)
            }
        )
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