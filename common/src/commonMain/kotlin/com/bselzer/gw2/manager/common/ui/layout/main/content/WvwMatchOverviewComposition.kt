package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.router.bringToFront
import com.bselzer.gw2.manager.common.ui.base.ShouldLayoutHorizontally
import com.bselzer.gw2.manager.common.ui.layout.chart.content.ChartComposition
import com.bselzer.gw2.manager.common.ui.layout.common.AbsoluteBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.common.BorderedCard
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.content.ContestedAreasComposition
import com.bselzer.gw2.manager.common.ui.layout.host.content.LocalMainRouter
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MainConfig
import com.bselzer.gw2.manager.common.ui.layout.main.content.match.overview.OwnerOverviewComposition
import com.bselzer.gw2.manager.common.ui.layout.main.content.match.overview.SelectedWorldComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMatchOverviewViewModel
import com.bselzer.ktx.compose.ui.layout.column.ColumnPresenter
import com.bselzer.ktx.compose.ui.layout.column.spacedColumnProjector
import com.bselzer.ktx.function.collection.buildArray

class WvwMatchOverviewComposition(
    model: WvwMatchOverviewViewModel
) : MainChildComposition<WvwMatchOverviewViewModel>(model),
    ContestedAreasComposition<WvwMatchOverviewViewModel> {
    @Composable
    override fun WvwMatchOverviewViewModel.Content(modifier: Modifier) = AbsoluteBackgroundImage(
        modifier = Modifier.fillMaxSize().then(modifier),
        contentAlignment = Alignment.TopCenter,
    ) {
        val spacing = if (ShouldLayoutHorizontally) 5.dp else 15.dp
        val padding = PaddingValues(vertical = if (ShouldLayoutHorizontally) 0.dp else 20.dp)
        spacedColumnProjector(
            thickness = spacing,
            presenter = ColumnPresenter.CenteredHorizontally
        ).Projection(
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(padding),
            content = buildContent()
        )
    }

    @Composable
    private fun WvwMatchOverviewViewModel.buildContent(): Array<@Composable ColumnScope.() -> Unit> = buildArray {
        add { SelectedWorld() }

        if (overviews.any()) {
            val content = if (ShouldLayoutHorizontally) horizontalContent() else verticalContent()
            addAll(content)
        }
    }

    @Composable
    private fun WvwMatchOverviewViewModel.horizontalContent(): List<@Composable ColumnScope.() -> Unit> = buildList {
        add {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Chart()
                Overview()
            }
        }

        add { ContestedAreas() }
    }

    @Composable
    private fun WvwMatchOverviewViewModel.verticalContent(): List<@Composable ColumnScope.() -> Unit> = buildList {
        add { Overview() }
        add { ContestedAreas() }
    }

    @Composable
    private fun WvwMatchOverviewViewModel.Chart() = chart?.let { chart ->
        val modifier = Modifier.routeOnClick(MainConfig.WvwMatchStatisticsConfig)
        ChartComposition(model = chart).Content(modifier = modifier)
    }

    @Composable
    private fun WvwMatchOverviewViewModel.SelectedWorld() = BorderedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        SelectedWorldComposition(selectedWorld).Content()
    }

    @Composable
    private fun WvwMatchOverviewViewModel.ContestedAreas() = BorderedCard(
        modifier = Modifier.fillMaxWidth().routeOnClick(MainConfig.WvwMatchContestedAreasConfig)
    ) {
        ContestedAreasContent()
    }

    @Composable
    private fun WvwMatchOverviewViewModel.Overview() = BorderedCard(
        modifier = Modifier.fillMaxWidth().routeOnClick(MainConfig.WvwMatchStatisticsConfig)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            overviews.forEach { overview ->
                OwnerOverviewComposition(overview).Content()
            }
        }
    }

    @Composable
    private fun Modifier.routeOnClick(config: MainConfig): Modifier {
        val router = LocalMainRouter.current
        return clickable { router.bringToFront(config) }
    }
}