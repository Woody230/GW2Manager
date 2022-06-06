package com.bselzer.gw2.manager.common.ui.layout.main.content.map

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.dependency.LocalTheme
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.image.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.image.Content
import com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective.*
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ObjectiveViewModel
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.resource.ui.layout.icon.expansionIconInteractor
import com.bselzer.ktx.compose.ui.layout.background.image.BackgroundImage
import com.bselzer.ktx.compose.ui.layout.centeredtext.CenteredTextInteractor
import com.bselzer.ktx.compose.ui.layout.centeredtext.CenteredTextPresenter
import com.bselzer.ktx.compose.ui.layout.centeredtext.CenteredTextProjector
import com.bselzer.ktx.compose.ui.layout.column.ColumnPresenter
import com.bselzer.ktx.compose.ui.layout.column.spacedColumnProjector
import com.bselzer.ktx.compose.ui.layout.icon.IconProjector
import com.bselzer.ktx.compose.ui.layout.merge.TriState
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import com.bselzer.ktx.compose.ui.layout.text.TextPresenter
import com.bselzer.ktx.datetime.format.minuteFormat
import com.bselzer.ktx.function.collection.buildArray
import com.bselzer.ktx.resource.KtxResources
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
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

    @Composable
    override fun ObjectiveViewModel.Content() = BackgroundImage(
        modifier = Modifier.fillMaxSize(),
        painter = absoluteBackgroundPainter
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
                .fillMaxWidth()
                .verticalScroll(verticalScroll)
        ) { index ->
            when (tabs[index]) {
                ObjectiveTabType.DETAILS -> DetailsColumn()
                ObjectiveTabType.AUTOMATIC_UPGRADES -> AutomaticUpgradeColumn()
                ObjectiveTabType.GUILD_IMPROVEMENTS -> GuildUpgradeColumn(tiers = improvementTiers)
                ObjectiveTabType.GUILD_TACTICS -> GuildUpgradeColumn(tiers = tacticTiers)
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
        if (shouldShowUpgradeTiers) {
            add(ObjectiveTabType.AUTOMATIC_UPGRADES)
        }

        if (shouldShowImprovementTiers) {
            add(ObjectiveTabType.GUILD_IMPROVEMENTS)
        }

        if (shouldShowTacticTiers) {
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
                add { icon.ImageContent() }
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

            claim?.let { claim ->
                add {
                    InfoCard { Claim(claim) }
                }
            }
        }
    )

    @Composable
    private fun Icon.ImageContent(modifier: Modifier = Modifier, color: Color? = null) = AsyncImage(
        image = link,
        width = width,
        height = height,
        description = description,
        color = color ?: this.color,
        alpha = alpha.collectAsState(DefaultAlpha).value
    ).Content(modifier)

    /**
     * Lays out the column for automatic upgrades.
     */
    @Composable
    private fun ObjectiveViewModel.AutomaticUpgradeColumn() = ContentColumn(
        content = buildArray {
            automaticUpgradeTiers.forEach { tier ->
                add {
                    upgradeTierCard(
                        tierIcon = tier.icon,
                        tierDescription = tier.icon.description ?: "".desc(),
                        upgrades = tier.upgrades
                    )
                }
            }
        }
    )

    /**
     * Lays out the column for guild upgrades.
     */
    @Composable
    private fun GuildUpgradeColumn(tiers: Collection<GuildUpgradeTier>) = ContentColumn(
        content = buildArray {
            tiers.map { tier ->
                // TODO move to model
                // TODO translated
                val description: StringDesc = if (tier.startTime == null) {
                    // If there is no time, then there must be no claim.
                    "Not Claimed".desc()
                } else {
                    val remaining = tier.remaining.collectAsState(initial = tier.holdingPeriod).value

                    // If there is remaining time then display it, otherwise display the amount of time that was needed for this tier to unlock.
                    val holdFor = AppResources.strings.hold_for.format(remaining.minuteFormat())
                    val heldFor = AppResources.strings.held_for.format(tier.holdingPeriod.minuteFormat())
                    if (remaining.isPositive()) holdFor else heldFor
                }

                add {
                    upgradeTierCard(
                        tierIcon = tier.icon,
                        tierDescription = description,
                        upgrades = tier.upgrades,

                        // Tier image is completely white so it must be converted to black for light mode.
                        color = if (LocalTheme.current == Theme.LIGHT) Color.Black else null
                    )
                }
            }
        }
    )

    /**
     * Lays out a card wrapping the tier header with the ability to expand to display the associated upgrades.
     */
    @Composable
    private fun ColumnScope.upgradeTierCard(
        tierIcon: Icon,
        tierDescription: StringDesc,
        upgrades: Collection<Upgrade>,
        color: Color? = null
    ) {
        val isExpanded = remember { mutableStateOf(false) }
        InfoCard(
            modifier = Modifier.clickable {
                isExpanded.value = !isExpanded.value
            }
        ) {
            spacedColumnProjector(
                thickness = 15.dp,
                presenter = ColumnPresenter(prepend = TriState.TRUE, append = TriState.TRUE)
            ).Projection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 5.dp),
                content = buildArray {
                    // TODO (un)lock icon?
                    add {
                        upgradeTier(image = tierIcon, description = tierDescription, isExpanded = isExpanded.value, color = color)
                    }

                    if (isExpanded.value) {
                        // Only show the upgrade content when expanded to save space.
                        upgrades.forEach { upgrade ->
                            add {
                                upgrade(image = upgrade.icon, name = upgrade.name, description = upgrade.icon.description ?: "".desc())
                            }
                        }
                    }
                }
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
            BackgroundImage(
                painter = relativeBackgroundPainter,
                presenter = relativeBackgroundPresenter,
                contentAlignment = Alignment.Center,
                content = content
            )
        }
    }

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
                        start = TextInteractor(first.localized()),
                        end = TextInteractor(second.localized())
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

    /**
     * Lays out the guild claim information.
     */
    @Composable
    private fun Claim(claim: Claim) = Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = claim.claimedAt.localized(), textAlign = TextAlign.Center)
        Text(text = claim.claimedBy.localized(), textAlign = TextAlign.Center)
        claim.icon.ImageContent()
    }

    /**
     * Lays out the header representing a tier of upgrades.
     */
    @Composable
    private fun upgradeTier(image: Icon, description: StringDesc, isExpanded: Boolean, color: Color? = null) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (icon, descriptor, expansion) = createRefs()
            image.ImageContent(color = color, modifier = Modifier.constrainAs(icon) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                bottom.linkTo(parent.bottom)
            })
            Text(
                text = description.localized(),
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(descriptor) {
                    top.linkTo(parent.top)
                    start.linkTo(icon.end, margin = 5.dp)
                    end.linkTo(expansion.start, margin = 5.dp)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
            )

            IconProjector(
                interactor = expansionIconInteractor(isExpanded)
            ).Projection(
                modifier = Modifier.constrainAs(expansion) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            )
        }
    }

    /**
     * Lays out the image and description of an individual upgrade.
     */
    @Composable
    private fun upgrade(image: Icon, name: StringDesc, description: StringDesc) = Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        image.ImageContent()
        Spacer(modifier = Modifier.width(25.dp))
        Column {
            Text(text = name.localized(), style = MaterialTheme.typography.subtitle1)
            Text(text = description.localized(), style = MaterialTheme.typography.body2)
        }
    }
}