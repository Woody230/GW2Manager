package com.bselzer.gw2.manager.common.ui.layout.main.content.map

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.AbsoluteBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.custom.objective.content.ObjectiveOverviewComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.content.UpgradeTiersComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ObjectiveViewModel
import com.bselzer.ktx.compose.resource.strings.localized
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
class ObjectiveComposition(model: ObjectiveViewModel) : ViewModelComposition<ObjectiveViewModel>(model) {

    // TODO standardize capitalization of text, particularly for anything from the api -- for example, for French fortified is not capitalized while secured/reinforced are
    @Composable
    override fun ObjectiveViewModel.Content(modifier: Modifier) = AbsoluteBackgroundImage(
        modifier = Modifier.fillMaxSize().then(modifier),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val state = rememberPagerState()
            val selectedIndex = state.currentPage

            PagerTabs(selectedIndex, state)
            Pager(state, selectedIndex)
        }
    }

    @Composable
    private fun ObjectiveViewModel.Pager(
        state: PagerState,
        selectedIndex: Int
    ) {
        val verticalScroll = rememberScrollState()
        verticalScroll.ResetOnPageChange(selectedIndex)

        HorizontalPager(
            count = currentTabs().size,
            state = state,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScroll)
        ) { index ->
            PagerContent(index)
        }
    }

    @Composable
    private fun ObjectiveViewModel.PagerContent(index: Int) {
        val modifier = Modifier.fillMaxWidth()
        when (currentTabs()[index]) {
            ObjectiveTabType.DETAILS -> ObjectiveOverviewComposition(overview).Content(modifier = modifier)
            ObjectiveTabType.AUTOMATIC_UPGRADES -> UpgradeTiersComposition(automaticUpgradeTiers).Content(modifier = modifier)
            ObjectiveTabType.GUILD_IMPROVEMENTS -> UpgradeTiersComposition(improvementTiers).Content(modifier = modifier)
            ObjectiveTabType.GUILD_TACTICS -> UpgradeTiersComposition(tacticTiers).Content(modifier = modifier)
        }
    }

    @Composable
    private fun ScrollState.ResetOnPageChange(
        selectedIndex: Int
    ) = LaunchedEffect(selectedIndex) {
        // Reset to the top of the page when changing pages.
        animateScrollTo(0)
    }


    @Composable
    private fun ObjectiveViewModel.PagerTabs(
        selectedIndex: Int,
        state: PagerState
    ) = ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = Modifier.fillMaxWidth()
    ) {
        currentTabs().forEachIndexed { index, type ->
            PagerTab(type, index, selectedIndex, state)
        }
    }

    @Composable
    private fun PagerTab(
        type: ObjectiveTabType,
        index: Int,
        selectedIndex: Int,
        state: PagerState
    ) {
        val scope = rememberCoroutineScope()
        Tab(
            text = { Text(text = type.stringDesc().localized()) },
            selected = index == selectedIndex,
            onClick = {
                scope.launch { state.animateScrollToPage(index) }
            },
        )
    }

    /**
     * Gets the currently enabled tabs.
     */
    @Composable
    private fun ObjectiveViewModel.currentTabs(): List<ObjectiveTabType> = mutableListOf<ObjectiveTabType>().apply {
        add(ObjectiveTabType.DETAILS)

        // Only add the remaining tabs if they have been enabled and their applicable data is available.
        if (automaticUpgradeTiers.shouldShowTiers) {
            add(ObjectiveTabType.AUTOMATIC_UPGRADES)
        }

        if (improvementTiers.shouldShowTiers) {
            add(ObjectiveTabType.GUILD_IMPROVEMENTS)
        }

        if (tacticTiers.shouldShowTiers) {
            add(ObjectiveTabType.GUILD_TACTICS)
        }
    }
}