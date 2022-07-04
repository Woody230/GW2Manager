package com.bselzer.gw2.manager.common.ui.layout.main.content.map

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
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.resource.KtxResources
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch

class ObjectiveComposition(model: ObjectiveViewModel) : ViewModelComposition<ObjectiveViewModel>(model) {
    private enum class ObjectiveTabType {
        DETAILS,
        AUTOMATIC_UPGRADES,
        GUILD_IMPROVEMENTS,
        GUILD_TACTICS;

        fun stringDesc(): StringDesc = when (this) {
            DETAILS -> KtxResources.strings.details
            AUTOMATIC_UPGRADES -> Gw2Resources.strings.automatic_upgrades
            GUILD_IMPROVEMENTS -> Gw2Resources.strings.guild_improvements
            GUILD_TACTICS -> Gw2Resources.strings.guild_tactics
        }.desc()
    }

    // TODO standardize capitalization of text, particularly for anything from the api -- for example, for French fortified is not capitalized while secured/reinforced are

    @Composable
    override fun ObjectiveViewModel.Content(modifier: Modifier) = AbsoluteBackgroundImage(
        modifier = Modifier.fillMaxSize().then(modifier),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Pager()
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    private fun ObjectiveViewModel.Pager() {
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState()
        val selectedIndex = pagerState.currentPage
        val tabs = currentTabs()
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    text = { Text(text = tab.stringDesc().localized()) },
                    selected = index == selectedIndex,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }

        val verticalScroll = rememberScrollState()
        HorizontalPager(
            count = tabs.size,
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScroll)
        ) { index ->
            val modifier = Modifier.fillMaxWidth()
            when (tabs[index]) {
                ObjectiveTabType.DETAILS -> ObjectiveOverviewComposition(overview).Content(modifier = modifier)
                ObjectiveTabType.AUTOMATIC_UPGRADES -> UpgradeTiersComposition(automaticUpgradeTiers).Content(modifier = modifier)
                ObjectiveTabType.GUILD_IMPROVEMENTS -> UpgradeTiersComposition(improvementTiers).Content(modifier = modifier)
                ObjectiveTabType.GUILD_TACTICS -> UpgradeTiersComposition(tacticTiers).Content(modifier = modifier)
            }
        }

        LaunchedEffect(selectedIndex) {
            // Reset to the top of the page when changing pages.
            verticalScroll.animateScrollTo(0)
        }
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