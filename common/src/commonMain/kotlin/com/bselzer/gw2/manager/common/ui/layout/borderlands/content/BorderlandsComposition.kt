package com.bselzer.gw2.manager.common.ui.layout.borderlands.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.layout.borderlands.model.DataSet
import com.bselzer.gw2.manager.common.ui.layout.borderlands.viewmodel.BorderlandsViewModel
import com.bselzer.gw2.manager.common.ui.layout.common.RelativeBackgroundImage
import com.bselzer.ktx.compose.resource.strings.localized
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
interface BorderlandsComposition<Model, Data> where Model : BorderlandsViewModel<Data> {
    @Composable
    fun Model.BorderlandsContent() = Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // TODO legend that maps colors to worlds below the pager (add home icon?)
        val state = rememberPagerState()
        PagerTabs(state)
        Pager(state)
    }

    @Composable
    fun Model.Content() = RelativeBackgroundImage(
        modifier = Modifier.fillMaxSize(),
    ) {

    }

    @Composable
    private fun Model.PagerTabs(state: PagerState) = ScrollableTabRow(
        selectedTabIndex = state.currentPage,
        modifier = Modifier.fillMaxWidth()
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
    private fun Model.Pager(state: PagerState) = HorizontalPager(
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

            dataSets.getOrElse(index) { defaultDataSet }.data.Content()

            Spacer(modifier = Modifier.height(5.dp))
        }
    }

    @Composable
    fun Data.Content()
}