package com.bselzer.gw2.manager.android.wvw

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bselzer.gw2.manager.android.common.BasePage
import com.bselzer.gw2.manager.android.wvw.WvwSelectedObjectivePage.ObjectivePageType.*
import com.bselzer.gw2.manager.common.state.core.Gw2State
import com.bselzer.gw2.manager.common.state.selected.ClaimState
import com.bselzer.gw2.manager.common.state.selected.SelectedDataState
import com.bselzer.gw2.manager.common.state.selected.WvwSelectedState
import com.bselzer.gw2.manager.common.state.selected.overview.OverviewState
import com.bselzer.gw2.manager.common.state.selected.upgrade.GuildUpgradeTierState
import com.bselzer.gw2.manager.common.state.selected.upgrade.UpgradeState
import com.bselzer.gw2.manager.common.ui.composable.ImageContent
import com.bselzer.gw2.manager.common.ui.composable.ImageState
import com.bselzer.gw2.manager.common.ui.composable.LocalTheme
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.compose.ui.appbar.ExpansionIcon
import com.bselzer.ktx.compose.ui.container.DividedColumn
import com.bselzer.ktx.datetime.timer.minuteFormat
import com.bselzer.ktx.function.objects.userFriendly
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

class WvwSelectedObjectivePage(
    private val state: WvwSelectedState,
) : BasePage() {
    private enum class ObjectivePageType {
        DETAILS,
        AUTOMATIC_UPGRADES,
        GUILD_IMPROVEMENTS,
        GUILD_TACTICS
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    override fun Gw2State.Content() = Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
                    text = { Text(text = tab.userFriendly()) },
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
                .fillMaxWidth()
                .verticalScroll(verticalScroll)
        ) { index ->
            when (tabs[index]) {
                DETAILS -> DetailsColumn()
                AUTOMATIC_UPGRADES -> AutomaticUpgradeColumn()
                GUILD_IMPROVEMENTS -> GuildUpgradeColumn(tiers = state.improvementTiers.value)
                GUILD_TACTICS -> GuildUpgradeColumn(tiers = state.tacticTiers.value)
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
    private fun currentTabs(): List<ObjectivePageType> = mutableListOf<ObjectivePageType>().apply {
        add(DETAILS)

        // Only add the remaining tabs if they have been enabled and their applicable data is available.
        if (state.shouldShowUpgradeTiers.value) {
            add(AUTOMATIC_UPGRADES)
        }

        if (state.shouldShowImprovementTiers.value) {
            add(GUILD_IMPROVEMENTS)
        }

        if (state.shouldShowTacticTiers.value) {
            add(GUILD_TACTICS)
        }
    }

    /**
     * Lays out a column for wrapping the content of each [ObjectivePageType].
     */
    @Composable
    private fun ContentColumn(vararg contents: @Composable ColumnScope.() -> Unit) = DividedColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
        divider = { Spacer(modifier = Modifier.height(10.dp)) },
        prepend = true,
        append = true,
        contents = contents
    )

    /**
     * Lays out the core details of the objective.
     */
    @Composable
    private fun DetailsColumn() = ContentColumn(
        // TODO chat link?
        { state.image.value?.ImageContent() },
        { state.overview.value?.let { InfoCard { Overview(it) } } },
        { state.data.value?.let { InfoCard { Data(it) } } },
        { state.claim.value?.let { InfoCard { Claim(it) } } }
    )

    /**
     * Lays out the column for automatic upgrades.
     */
    @Composable
    private fun AutomaticUpgradeColumn() = ContentColumn(
        contents = state.automaticUpgradeTiers.value.map { tier ->
            upgradeTierCard(
                tierIcon = tier,
                tierDescription = tier.description,
                upgrades = tier.upgrades
            )
        }.toTypedArray()
    )

    /**
     * Lays out the column for guild upgrades.
     */
    @Composable
    private fun GuildUpgradeColumn(tiers: Collection<GuildUpgradeTierState>) = ContentColumn(
        contents = tiers.map { tier ->
            val description: String = if (tier.startTime == null) {
                // If there is no time, then there must be no claim.
                "Not Claimed"
            } else {
                val remaining by tier.remaining.collectAsState(initial = tier.holdingPeriod)

                // If there is remaining time then display it, otherwise display the amount of time that was needed for this tier to unlock.
                if (remaining.isPositive()) "Hold for ${remaining.minuteFormat()}" else "Held for ${tier.holdingPeriod.minuteFormat()}"
            }

            upgradeTierCard(
                tierIcon = tier,
                tierDescription = description,
                upgrades = tier.upgrades,

                // Tier image is completely white so it must be converted to black for light mode.
                color = if (LocalTheme.current == Theme.LIGHT) Color.Black else null
            )
        }.toTypedArray()
    )

    /**
     * Lays out a card wrapping the tier header with the ability to expand to display the associated upgrades.
     */
    @Composable
    private fun upgradeTierCard(
        tierIcon: ImageState,
        tierDescription: String,
        upgrades: Collection<UpgradeState>,
        color: Color? = null
    ): @Composable ColumnScope.() -> Unit = {
        val isExpanded = remember { mutableStateOf(false) }
        InfoCard(
            modifier = Modifier
        ) {
            // TODO (un)lock icon?
            val contents = mutableListOf(upgradeTier(image = tierIcon, description = tierDescription, isExpanded = isExpanded, color = color))
            if (isExpanded.value) {
                // Only show the upgrade content when expanded to save space.
                contents.addAll(upgrades.map { upgrade -> upgrade(image = upgrade, name = upgrade.name, description = upgrade.description) })
            }

            DividedColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 5.dp),
                prepend = true,
                append = true,
                divider = { Spacer(modifier = Modifier.height(15.dp)) },
                contents = contents.toTypedArray()
            )
        }
    }

    /**
     * Lays out a card wrapping the underlying [content].
     */
    @Composable
    private fun InfoCard(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
        val border = 3.dp
        Card(
            elevation = 10.dp,
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth(.80f)
                .wrapContentHeight()
                .border(width = border, color = Color.Black)
                .padding(all = border)
                .then(modifier)
        ) {
            RelativeBackground(content = content)
        }
    }

    /**
     * Lays out the overview of information about the selected objective.
     */
    @Composable
    private fun Overview(overview: OverviewState) = Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // TODO images alongside the text?
        Text(text = overview.name, textAlign = TextAlign.Center)

        overview.map?.let { map ->
            Text(text = map.name, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = map.color)
        }

        overview.owner?.let { owner ->
            Text(text = owner.name, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = owner.color)
        }

        overview.flipped?.let { flipped ->
            Text(text = flipped, textAlign = TextAlign.Center)
        }
    }

    /**
     * Lays out the data points such as point information and upgrade progress.
     */
    @Composable
    private fun Data(data: SelectedDataState) = Column {
        @Composable
        fun Pair<String, String>?.Row() {
            this?.let {
                BoldCenteredRow(startValue = first, endValue = second)
            }
        }

        listOf(
            data.pointsPerTick,
            data.pointsPerCapture,
            data.yaks,
            data.upgrade
        ).forEach { pair -> pair.Row() }
    }

    /**
     * Lays out the guild claim information.
     */
    @Composable
    private fun Claim(claim: ClaimState) = Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = claim.claimedAt, textAlign = TextAlign.Center)
        Text(text = claim.claimedBy, textAlign = TextAlign.Center)
        claim.ImageContent()
    }

    /**
     * Lays out the header representing a tier of upgrades.
     */
    @Composable
    private fun upgradeTier(image: ImageState, description: String, isExpanded: MutableState<Boolean>, color: Color? = null): @Composable ColumnScope.() -> Unit = {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (icon, descriptor, expansion) = createRefs()
            image.ImageContent(color = color, modifier = Modifier.constrainAs(icon) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                bottom.linkTo(parent.bottom)
            })
            Text(text = description, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold, modifier = Modifier.constrainAs(descriptor) {
                top.linkTo(parent.top)
                start.linkTo(icon.end, margin = 5.dp)
                end.linkTo(expansion.start, margin = 5.dp)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
            })
            ExpansionIcon(state = isExpanded, modifier = Modifier.constrainAs(expansion) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            })
        }
    }

    /**
     * Lays out the image and description of an individual upgrade.
     */
    @Composable
    private fun upgrade(image: ImageState, name: String, description: String): @Composable ColumnScope.() -> Unit = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            image.ImageContent()
            Spacer(modifier = Modifier.width(25.dp))
            Column {
                Text(text = name, style = MaterialTheme.typography.subtitle1)
                Text(text = description, style = MaterialTheme.typography.body2)
            }
        }
    }
}