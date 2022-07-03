package com.bselzer.gw2.manager.common.ui.layout.borderlands.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.layout.borderlands.model.DataSet
import com.bselzer.gw2.manager.common.ui.layout.borderlands.viewmodel.BorderlandsViewModel
import com.bselzer.ktx.compose.resource.strings.localized
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
interface BorderlandsComposition<Data> {
    @Composable
    fun BorderlandsViewModel<Data>.BorderlandsContent(modifier: Modifier = Modifier) = Column(
        modifier = modifier
    ) {
        // TODO legend that maps colors to worlds below the pager (add home icon?)
        val state = rememberPagerState()
        PagerTabs(state)
        Pager(state)
    }

    @Composable
    private fun BorderlandsViewModel<Data>.PagerTabs(state: PagerState) = ScrollableTabRow(
        selectedTabIndex = state.currentPage,
        modifier = Modifier.fillMaxWidth(),
        indicator = { tabPositions ->
            // NOTE: overriding to not throw an error when unable to get the position because the index is out of bounds (such as potentially during configuration change)
            val tabPosition = tabPositions.getOrNull(state.currentPage)
            TabRowDefaults.Indicator(
                if (tabPosition == null) Modifier else Modifier.tabIndicatorOffset(tabPosition)
            )
        }
    ) {
        dataSets.forEachIndexed { index, dataSet ->
            dataSet.PagerTab(index, state)
        }
    }

    @Composable
    private fun DataSet<Data>.PagerTab(index: Int, state: PagerState) {
        val scope = rememberCoroutineScope()
        Tab(
            text = { Text(text = title.localized()) },
            selected = index == state.currentPage,
            onClick = {
                scope.launch {
                    state.animateScrollToPage(index)
                }
            }
        )
    }

    @Composable
    private fun BorderlandsViewModel<Data>.Pager(state: PagerState) = HorizontalPager(
        count = dataSets.size,
        state = state,
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) { index ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(5.dp))

            val data = dataSets.getOrElse(index) { defaultDataSet }.data
            Content(index, data)

            Spacer(modifier = Modifier.height(5.dp))
        }
    }

    @Composable
    fun ColumnScope.Content(index: Int, data: Data)
}