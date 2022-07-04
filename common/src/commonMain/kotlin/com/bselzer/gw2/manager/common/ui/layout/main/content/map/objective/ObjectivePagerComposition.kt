package com.bselzer.gw2.manager.common.ui.layout.main.content.map.objective

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.objective.content.ObjectiveOverviewComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.content.UpgradeTiersComposition
import com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective.ObjectiveTabType
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ObjectiveViewModel
import com.bselzer.ktx.compose.resource.strings.localized
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
class ObjectivePagerComposition constructor(
    model: ObjectiveViewModel,
    private val state: PagerState,
    private val verticalScroll: ScrollState
) : ModelComposition<ObjectiveViewModel>(model) {
    private val selectedIndex: Int
        get() = state.currentPage

    @Composable
    override fun ObjectiveViewModel.Content(modifier: Modifier) = Column(
        modifier = modifier
    ) {
        ResetScrollOnPageChange()
        PagerTabs()
        Pager()
    }

    @Composable
    private fun ObjectiveViewModel.Pager() = HorizontalPager(
        count = currentTabs.size,
        state = state,
        verticalAlignment = Alignment.Top,
        modifier = Modifier.verticalScroll(verticalScroll)
    ) { index ->
        PagerContent(index)
    }

    @Composable
    private fun ObjectiveViewModel.PagerContent(index: Int) {
        val modifier = Modifier.fillMaxWidth()
        when (currentTabs[index]) {
            ObjectiveTabType.DETAILS -> ObjectiveOverviewComposition(overview).Content(modifier = modifier)
            ObjectiveTabType.AUTOMATIC_UPGRADES -> UpgradeTiersComposition(automaticUpgradeTiers).Content(modifier = modifier)
            ObjectiveTabType.GUILD_IMPROVEMENTS -> UpgradeTiersComposition(improvementTiers).Content(modifier = modifier)
            ObjectiveTabType.GUILD_TACTICS -> UpgradeTiersComposition(tacticTiers).Content(modifier = modifier)
        }
    }

    @Composable
    private fun ResetScrollOnPageChange() = LaunchedEffect(selectedIndex) {
        // Reset to the top of the page when changing pages.
        verticalScroll.animateScrollTo(0)
    }

    @Composable
    private fun ObjectiveViewModel.PagerTabs() = ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = Modifier.fillMaxWidth()
    ) {
        currentTabs.forEachIndexed { index, type ->
            type.PagerTab(index)
        }
    }

    @Composable
    private fun ObjectiveTabType.PagerTab(
        index: Int,
    ) {
        val scope = rememberCoroutineScope()
        Tab(
            text = { Text(text = stringDesc().localized()) },
            selected = index == selectedIndex,
            onClick = {
                scope.launch { state.animateScrollToPage(index) }
            },
        )
    }
}