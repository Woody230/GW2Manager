package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bselzer.gw2.manager.common.ui.layout.image.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.image.Content
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.Chart
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.ChartData
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMatchViewModel
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.geometry.shape.ArcShape
import com.bselzer.ktx.compose.ui.layout.background.image.BackgroundImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

class WvwMatchComposition(model: WvwMatchViewModel) : MainChildComposition<WvwMatchViewModel>(model) {
    @OptIn(ExperimentalPagerApi::class)
    @Composable
    override fun WvwMatchViewModel.Content() = BackgroundImage(
        modifier = Modifier.fillMaxSize(),
        painter = absoluteBackgroundPainter,
        presenter = absoluteBackgroundPresenter
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (tabs, pager, indicators) = createRefs()
            val allCharts = charts.toList()

            // Lay out the tabs representing each map.
            val selectedIndex = remember { mutableStateOf(0) }
            ScrollableTabRow(
                selectedTabIndex = selectedIndex.value,
                modifier = Modifier.constrainAs(tabs) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            ) {
                // TODO since overview has the extra chart for victory points, feels awkward to swap between overview and a borderland and get a different type of chart
                allCharts.forEachIndexed { index, charts ->
                    Tab(
                        text = { Text(text = charts.title.localized()) },
                        selected = index == selectedIndex.value,
                        onClick = { selectedIndex.value = index }
                    )
                }
            }

            // Lay out the charts for the currently selected map.
            val pagerState = rememberPagerState()
            val selectedCharts = allCharts.getOrElse(selectedIndex.value) { defaultCharts }
            HorizontalPager(
                count = selectedCharts.charts.size,
                state = pagerState,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .constrainAs(pager) {
                        top.linkTo(tabs.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(indicators.top)
                        height = Dimension.fillToConstraints
                    }
            ) { index ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    PieChart(chart = selectedCharts.charts.toList()[index])
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }

            // Lay out the indicators representing each chart for the selected map.
            HorizontalPagerIndicator(
                pagerState = pagerState,
                inactiveColor = MaterialTheme.colors.primary,
                activeColor = selectedCharts.color,
                modifier = Modifier.constrainAs(indicators) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom, margin = 5.dp)
                }
            )
        }
    }

    /**
     * Lays out a pie chart with the data describing it.
     */
    @Composable
    private fun WvwMatchViewModel.PieChart(chart: Chart) {
        val width = 512
        val height = 512
        Box {
            AsyncImage(
                image = chart.background,
                width = width,
                height = height,
            ).Content(useProgressIndicator = true)

            chart.slices.forEach { slice ->
                AsyncImage(
                    image = slice.image,
                    width = width,
                    height = height,
                    description = slice.description
                ).Content(
                    useProgressIndicator = true,
                    modifier = Modifier.clip(ArcShape(slice.startAngle, slice.endAngle)),
                )
            }

            // Add the dividers between the slices.
            chart.slices.map { slice -> slice.startAngle }.forEach { angle ->
                AsyncImage(
                    image = chart.divider,
                    width = width,
                    height = height,
                ).Content(
                    modifier = Modifier.rotate(degrees = angle),
                )
            }
        }

        ChartDescription(chart = chart)
    }

    /**
     * Lays out a description of the chart with its associated data.
     */
    @Composable
    private fun WvwMatchViewModel.ChartDescription(chart: Chart) = BackgroundImage(
        painter = relativeBackgroundPainter,
        presenter = relativeBackgroundPresenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = chart.title.localized(),
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )

            // Show the data representing each slice.
            chart.data.forEach { data -> ChartData(data) }
        }
    }

    /**
     * Lays out the data associated with a slice.
     */
    @Composable
    private fun WvwMatchViewModel.ChartData(data: ChartData) {
        Text(
            text = data.owner.localized(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = data.color,
            textAlign = TextAlign.Center
        )
        Text(
            text = data.data.localized(),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(3.dp))
    }
}