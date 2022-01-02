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
import com.bselzer.gw2.manager.common.ui.composable.ImageContent
import com.bselzer.gw2.manager.common.ui.composable.ImageState
import com.bselzer.ktx.compose.ui.appbar.ExpansionIcon
import com.bselzer.ktx.compose.ui.container.DividedColumn
import com.bselzer.ktx.datetime.timer.minuteFormat
import com.bselzer.ktx.function.objects.userFriendly

class WvwSelectedObjectivePage(
    private val state: WvwSelectedState,
) : BasePage() {
    private enum class ObjectivePageType {
        DETAILS,
        AUTOMATIC_UPGRADES,
        GUILD_IMPROVEMENTS,
        GUILD_TACTICS
    }

    @Composable
    override fun Gw2State.Content() = Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        var selectedIndex by remember { mutableStateOf(0) }
        val tabs = currentTabs()
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            indicator = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    text = { Text(text = tab.userFriendly()) },
                    selected = index == selectedIndex,
                    onClick = { selectedIndex = index },
                )
            }
        }

        when (tabs[selectedIndex]) {
            DETAILS -> ContentColumn(
                { state.image.value?.ImageContent() },
                { state.overview.value?.let { InfoCard { Overview(it) } } },
                { state.data.value?.let { InfoCard { Data(it) } } },
                { state.claim.value?.let { InfoCard { Claim(it) } } }
            )
            AUTOMATIC_UPGRADES -> ContentColumn(
                contents = state.automaticUpgradeTiers.value.map { tier ->
                    tierCard(
                        tierIcon = tier,
                        tierDescription = "${tier.description} (${tier.yakRatio})",
                        upgradeContent = tier.upgrades.map { upgrade -> upgrade(image = upgrade, name = upgrade.name, description = upgrade.description) }
                    )
                }.toTypedArray()
            )
            GUILD_IMPROVEMENTS -> ContentColumn(
                contents = state.improvementTiers.value.map { tier ->
                    val remaining by tier.remaining.collectAsState(initial = tier.holdingPeriod)
                    tierCard(
                        tierIcon = tier,
                        tierDescription = if (remaining.isPositive()) "Hold for ${remaining.minuteFormat()}" else "Held for ${tier.holdingPeriod.minuteFormat()}",
                        upgradeContent = tier.upgrades.map { upgrade -> upgrade(image = upgrade, name = upgrade.name, description = upgrade.description) }
                    )
                }.toTypedArray()
            )
            GUILD_TACTICS -> ContentColumn(
                contents = state.tacticTiers.value.map { tier ->
                    val remaining by tier.remaining.collectAsState(initial = tier.holdingPeriod)
                    tierCard(
                        tierIcon = tier,
                        tierDescription = if (remaining.isPositive()) "Hold for ${remaining.minuteFormat()}" else "Held for ${tier.holdingPeriod.minuteFormat()}",
                        upgradeContent = tier.upgrades.map { upgrade -> upgrade(image = upgrade, name = upgrade.name, description = upgrade.description) }
                    )
                }.toTypedArray()
            )
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

    @Composable
    private fun ContentColumn(vararg contents: @Composable ColumnScope.() -> Unit) = DividedColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
        divider = { Spacer(modifier = Modifier.height(10.dp)) },
        prepend = true,
        append = true,
        contents = contents
    )

    @Composable
    private fun tierCard(
        tierIcon: ImageState,
        tierDescription: String,
        upgradeContent: Collection<@Composable ColumnScope.() -> Unit>
    ): @Composable ColumnScope.() -> Unit = {
        val isExpanded = remember { mutableStateOf(false) }
        InfoCard(
            modifier = Modifier
        ) {
            val contents = mutableListOf(upgradeTier(image = tierIcon, description = tierDescription, isExpanded = isExpanded))
            if (isExpanded.value) {
                contents.addAll(upgradeContent)
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

    @Composable
    private fun upgradeTier(image: ImageState, description: String, isExpanded: MutableState<Boolean>): @Composable ColumnScope.() -> Unit = {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (icon, descriptor, expansion) = createRefs()
            image.ImageContent(modifier = Modifier.constrainAs(icon) {
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