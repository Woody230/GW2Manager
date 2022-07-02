package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.router.bringToFront
import com.bselzer.gw2.manager.common.ui.layout.common.AbsoluteBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.common.BorderedCard
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.host.content.LocalDialogRouter
import com.bselzer.gw2.manager.common.ui.layout.image.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.image.Content
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.overview.Bloodlust
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.overview.Data
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.overview.Home
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.overview.OwnerOverview
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMatchOverviewViewModel
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.preference.section.spacedPreferenceColumnProjector
import com.bselzer.ktx.compose.ui.layout.preference.text.TextPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.text.TextPreferencePresenter
import com.bselzer.ktx.compose.ui.layout.preference.text.TextPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.row.spacedRowProjector
import com.bselzer.ktx.compose.ui.layout.text.TextPresenter
import com.bselzer.ktx.function.collection.buildArray

class WvwMatchOverviewComposition(model: WvwMatchOverviewViewModel) : MainChildComposition<WvwMatchOverviewViewModel>(model) {
    private companion object {
        val dataIconSize = DpSize(16.dp, 16.dp)
        val indicatorSize = DpSize(32.dp, 32.dp)
    }

    @Composable
    override fun WvwMatchOverviewViewModel.Content(modifier: Modifier) = AbsoluteBackgroundImage(
        modifier = Modifier.fillMaxSize().then(modifier),
        contentAlignment = Alignment.TopCenter,
    ) {
        spacedPreferenceColumnProjector().Projection(
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(paddingValues),
            content = buildArray {
                add { SelectedWorld() }

                if (overviews.any()) {
                    add { Overview() }
                }
            }
        )
    }

    /**
     * Lays out the selected world with the ability to show the dialog for a new selection.
     */
    @Composable
    private fun WvwMatchOverviewViewModel.SelectedWorld() = ModuleCard {
        val dialogRouter = LocalDialogRouter.current
        TextPreferenceProjector(
            interactor = TextPreferenceInteractor(
                painter = selectedWorld.image.painter(),
                title = selectedWorld.title.localized(),
                subtitle = selectedWorld.subtitle.localized(),
            ),
            presenter = TextPreferencePresenter(
                subtitle = TextPresenter(color = selectedWorld.color, fontWeight = FontWeight.Bold)
            )
        ).Projection(modifier = Modifier.clickable {
            // Open up the world selection dialog so that the user can pick another world.
            dialogRouter.bringToFront(DialogConfig.WorldSelectionConfig)
        })
    }

    /**
     * Lays out the overview for the selected world's match.
     */
    @Composable
    private fun WvwMatchOverviewViewModel.Overview() = ModuleCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            /*
            chart?.let { chart ->
                ChartComposition(model = chart).Content()
            }

            chartDescription?.let { description ->
                ChartDataComposition(model = description).Content()
            }
             */

            overviews.forEach { overview -> overview.Overview() }
        }
    }

    @Composable
    private fun OwnerOverview.Overview() = Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Name()
            DataPoints()
        }

        Icons()
    }

    @Composable
    private fun OwnerOverview.Name() = Text(
        text = owner.name.localized(),
        color = owner.color,
    )

    @Composable
    private fun OwnerOverview.DataPoints() = spacedRowProjector(
        thickness = 5.dp
    ).Projection(
        content = buildArray {
            add { victoryPoints.DataPoint() }
            add { pointsPerTick.DataPoint() }
            add { warScore.DataPoint() }
        }
    )

    @Composable
    private fun OwnerOverview.Icons() = spacedRowProjector(
        thickness = 5.dp
    ).Projection(
        content = buildArray {
            home?.let { home ->
                add { home.Icon() }
            }

            bloodlusts.forEach { bloodlust ->
                add { bloodlust.Icon() }
            }
        }
    )

    @Composable
    private fun Home.Icon() = AsyncImage(
        image = icon,
        description = null, // TODO description
        size = indicatorSize,
        color = color
    ).Content()

    @Composable
    private fun Bloodlust.Icon() = AsyncImage(
        image = icon,
        description = null, // TODO description
        size = indicatorSize,
        color = color
    ).Content()

    @Composable
    private fun Data.DataPoint() = Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            image = icon,
            description = description,
            size = dataIconSize
        ).Content()

        Text(text = data.localized())
    }

    /**
     * Lays out a card wrapping the underlying [content].
     */
    @Composable
    private fun ModuleCard(content: @Composable BoxScope.() -> Unit) = BorderedCard(
        content = content,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    )
}