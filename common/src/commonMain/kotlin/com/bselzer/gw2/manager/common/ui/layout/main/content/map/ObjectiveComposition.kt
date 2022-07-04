package com.bselzer.gw2.manager.common.ui.layout.main.content.map

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.*
import com.bselzer.gw2.manager.common.ui.layout.custom.claim.content.ClaimComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.content.UpgradeTiersComposition
import com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective.CoreData
import com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective.Overview
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ObjectiveViewModel
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.intl.LocalLocale
import com.bselzer.ktx.compose.ui.layout.centeredtext.CenteredTextInteractor
import com.bselzer.ktx.compose.ui.layout.centeredtext.CenteredTextPresenter
import com.bselzer.ktx.compose.ui.layout.centeredtext.CenteredTextProjector
import com.bselzer.ktx.compose.ui.layout.column.ColumnPresenter
import com.bselzer.ktx.compose.ui.layout.column.spacedColumnProjector
import com.bselzer.ktx.compose.ui.layout.merge.TriState
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import com.bselzer.ktx.compose.ui.layout.text.TextPresenter
import com.bselzer.ktx.compose.ui.layout.text.textInteractor
import com.bselzer.ktx.function.collection.buildArray
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

    private companion object {
        val objectiveSize: DpSize = DpSize(50.dp, 50.dp)
        val tierSize: DpSize = DpSize(75.dp, 75.dp)
        val upgradeSize: DpSize = DpSize(50.dp, 50.dp)
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
                ObjectiveTabType.DETAILS -> DetailsColumn()
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

    /**
     * Lays out a column for wrapping the content of each [ObjectiveTabType].
     */
    @Composable
    private fun ContentColumn(
        vararg content: @Composable ColumnScope.() -> Unit
    ) = spacedColumnProjector(
        thickness = 10.dp,
        presenter = ColumnPresenter(
            prepend = TriState.TRUE,
            append = TriState.TRUE,
            horizontalAlignment = Alignment.CenterHorizontally
        )
    ).Projection(
        modifier = Modifier.fillMaxWidth(),
        content = content
    )

    /**
     * Lays out the core details of the objective.
     */
    @Composable
    private fun ObjectiveViewModel.DetailsColumn() = ContentColumn(
        content = buildArray {
            // TODO chat link?
            icon?.let { icon ->
                // TODO objective images are mostly 32x32 and look awful as result of being scaled like this
                add {
                    AsyncImage(
                        image = icon.link,
                        description = icon.description,
                        size = objectiveSize,
                        color = icon.color
                    ).Content(progressIndication = ProgressIndication.ENABLED)
                }
            }

            overview?.let { overview ->
                add {
                    InfoCard { Overview(overview) }
                }
            }

            data?.let { data ->
                add {
                    InfoCard { Data(data) }
                }
            }

            if (claim.exists) {
                add {
                    InfoCard {
                        ClaimComposition(claim).Content()
                    }
                }
            }
        }
    )

    /**
     * Lays out a card wrapping the underlying [content].
     */
    @Composable
    private fun InfoCard(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) = BorderedCard(
        content = content,
        modifier = Modifier
            .fillMaxWidth(.90f)
            .wrapContentHeight()
            .then(modifier)
    )

    /**
     * Lays out the overview of information about the selected objective.
     */
    @Composable
    private fun Overview(overview: Overview) = Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // TODO images alongside the text?
        Text(text = overview.name.localized(), textAlign = TextAlign.Center)

        overview.map?.let { map ->
            Text(text = map.name.localized(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = map.color)
        }

        overview.owner?.let { owner ->
            Text(text = owner.name.localized(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = owner.color)
        }

        overview.flipped?.let { flipped ->
            Text(text = flipped.localized(), textAlign = TextAlign.Center)
        }
    }

    /**
     * Lays out the data points such as point information and upgrade progress.
     */
    @Composable
    private fun Data(data: CoreData) = Column {
        @Composable
        fun Pair<StringDesc, StringDesc>?.Row() {
            this?.let {
                CenteredTextProjector(
                    interactor = CenteredTextInteractor(
                        // TODO divider instead of colon? separate rows for first/secondary text?
                        start = TextInteractor(first.localized() + ":"),
                        end = second.localized().capitalize(LocalLocale.current).textInteractor()
                    ),
                    presenter = CenteredTextPresenter(
                        start = TextPresenter(fontWeight = FontWeight.Bold)
                    )
                ).Projection()
            }
        }

        listOf(
            data.pointsPerTick,
            data.pointsPerCapture,
            data.yaks,
            data.upgrade
        ).forEach { pair -> pair.Row() }
    }
}