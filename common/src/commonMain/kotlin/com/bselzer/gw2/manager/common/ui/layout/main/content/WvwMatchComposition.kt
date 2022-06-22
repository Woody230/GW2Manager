package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bselzer.gw2.manager.common.ui.layout.chart.content.ChartComposition
import com.bselzer.gw2.manager.common.ui.layout.common.AbsoluteBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMatchViewModel
import com.bselzer.ktx.compose.resource.strings.localized
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

class WvwMatchComposition(model: WvwMatchViewModel) : MainChildComposition<WvwMatchViewModel>(model) {
    @OptIn(ExperimentalPagerApi::class)
    @Composable
    override fun WvwMatchViewModel.Content() = AbsoluteBackgroundImage(
        modifier = Modifier.fillMaxSize(),
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
                    ChartComposition(model = selectedCharts.charts.toList()[index]).Content()
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
}