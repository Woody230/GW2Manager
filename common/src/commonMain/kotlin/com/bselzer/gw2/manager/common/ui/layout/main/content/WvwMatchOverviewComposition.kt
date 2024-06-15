package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.router.stack.bringToFront
import com.bselzer.gw2.manager.common.ui.base.ShouldLayoutHorizontally
import com.bselzer.gw2.manager.common.ui.layout.common.AbsoluteBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.common.BorderedCard
import com.bselzer.gw2.manager.common.ui.layout.custom.chart.content.ChartComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.contestedarea.content.ContestedAreasComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.owner.content.OwnerOverviewsComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.content.WorldComposition
import com.bselzer.gw2.manager.common.ui.layout.host.content.LocalMainRouter
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MainConfig
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMatchOverviewViewModel
import com.bselzer.ktx.compose.ui.layout.column.ColumnPresenter
import com.bselzer.ktx.compose.ui.layout.column.spacedColumnProjector
import com.bselzer.ktx.function.collection.buildArray

class WvwMatchOverviewComposition(
    model: WvwMatchOverviewViewModel
) : MainChildComposition<WvwMatchOverviewViewModel>(model),
    OwnerOverviewsComposition,
    ContestedAreasComposition {
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

        val content = if (ShouldLayoutHorizontally) horizontalContent() else verticalContent()
        addAll(content)
    }

    @Composable
    private fun WvwMatchOverviewViewModel.horizontalContent(): List<@Composable ColumnScope.() -> Unit> = buildList {
        add {
            Row(verticalAlignment = Alignment.CenterVertically) {
                VictoryPointChart()
                Overview()
            }
        }

        add {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PointsPerTickChart()
                ContestedArea()
            }
        }
    }

    @Composable
    private fun WvwMatchOverviewViewModel.verticalContent(): List<@Composable ColumnScope.() -> Unit> = buildList {
        add { Overview() }
        add { ContestedArea() }
    }

    @Composable
    private fun WvwMatchOverviewViewModel.VictoryPointChart() {
        val modifier = Modifier.routeOnClick(MainConfig.WvwMatchStatisticsConfig)
        ChartComposition(model = vpChart).Content(modifier = modifier)
    }

    @Composable
    private fun WvwMatchOverviewViewModel.PointsPerTickChart() {
        val modifier = Modifier.routeOnClick(MainConfig.WvwMatchContestedAreasConfig)
        ChartComposition(model = pptChart).Content(modifier = modifier)
    }

    @Composable
    private fun WvwMatchOverviewViewModel.SelectedWorld() = BorderedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        WorldComposition(selectedWorld).Content()
    }

    @Composable
    private fun WvwMatchOverviewViewModel.ContestedArea() = BorderedCard(
        modifier = Modifier.fillMaxWidth().routeOnClick(MainConfig.WvwMatchContestedAreasConfig)
    ) {
        ContestedAreas(Modifier)
    }

    @Composable
    private fun WvwMatchOverviewViewModel.Overview() = BorderedCard(
        modifier = Modifier.fillMaxWidth().routeOnClick(MainConfig.WvwMatchStatisticsConfig)
    ) {
        // TODO constraint layout to keep data in same horizontal position
        OwnerOverviews(Modifier)
    }

    @Composable
    private fun Modifier.routeOnClick(config: MainConfig): Modifier {
        val router = LocalMainRouter.current
        val modifier = Modifier.clickable { router.bringToFront(config) }
        return this.then(modifier)
    }
}