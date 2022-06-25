package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.layout.common.RelativeBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.image.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.image.Content
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.DataSet
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.Progress
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.Progression
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMatchViewModel
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.column.ColumnPresenter
import com.bselzer.ktx.compose.ui.layout.column.spacedColumnProjector
import com.bselzer.ktx.compose.ui.layout.spacer.Spacer
import com.bselzer.ktx.function.collection.buildArray
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
class WvwMatchComposition(model: WvwMatchViewModel) : MainChildComposition<WvwMatchViewModel>(model) {
    @Composable
    override fun WvwMatchViewModel.Content() = RelativeBackgroundImage(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            val state = rememberPagerState()
            PagerTabs(state)
            Pager(state)
        }
    }

    /**
     * Lays out the tabs for selecting a map in the pager.
     */
    @Composable
    private fun WvwMatchViewModel.PagerTabs(state: PagerState) {
        val scope = rememberCoroutineScope()
        ScrollableTabRow(
            selectedTabIndex = state.currentPage,
            modifier = Modifier.fillMaxWidth()
        ) {
            datasets.forEachIndexed { index, dataset ->
                Tab(
                    text = { Text(text = dataset.title.localized()) },
                    selected = index == state.currentPage,
                    onClick = {
                        scope.launch {
                            state.animateScrollToPage(index)
                        }
                    }
                )
            }
        }
    }

    /**
     * Lays out the information for the currently selected map.
     */
    @Composable
    private fun WvwMatchViewModel.Pager(state: PagerState) = HorizontalPager(
        count = datasets.size,
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

            datasets.getOrElse(index) { defaultData }.Overview()

            Spacer(modifier = Modifier.height(5.dp))
        }
    }

    /**
     * Lays out the information about a particular statistic for each owner.
     */
    @Composable
    private fun DataSet.Overview() = Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        spacedColumnProjector(
            thickness = 10.dp,
            presenter = ColumnPresenter.CenteredHorizontally
        ).Projection(
            modifier = Modifier.fillMaxWidth(),
            content = buildArray {
                progressions.forEach { progression ->
                    add {
                        progression.Header()
                        progression.Progress()
                    }
                }
            }
        )
    }

    /**
     * Lays out the header icon and title indicating the type of progress.
     */
    @Composable
    private fun Progression.Header() = Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            image = icon,
            size = DpSize(25.dp, 25.dp)
        ).Content()

        Spacer(width = 5.dp)

        Text(
            text = title.localized(),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h6,
        )
    }

    /**
     * Lays out the progress and counts about a particular statistic for each owner.
     */
    @Composable
    private fun Progression.Progress() = Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        progress.forEach { progress ->
            val percentage = progress.percentage.coerceAtLeast(0.05f)
            progress.Progress(
                modifier = Modifier.weight(percentage)
            )
        }
    }

    /**
     * Lays out the progress and count about a particular statistic for a particular owner.
     */
    @Composable
    private fun Progress.Progress(modifier: Modifier) = Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(3.dp).background(color = color)
        )

        Text(text = amount.toString())
    }
}