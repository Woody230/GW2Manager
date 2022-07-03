package com.bselzer.gw2.manager.common.ui.layout.main.content.match.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.image.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.image.Content
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.overview.Bloodlust
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.overview.Data
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.overview.Home
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.overview.OwnerOverview
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.row.spacedRowProjector
import com.bselzer.ktx.function.collection.buildArray

/**
 * Lays out the overview for the selected world's match.
 */
class OwnerOverviewComposition(
    model: OwnerOverview
) : ModelComposition<OwnerOverview>(model) {
    private companion object {
        val dataIconSize = DpSize(16.dp, 16.dp)
        val indicatorSize = DpSize(32.dp, 32.dp)
    }

    @Composable
    override fun OwnerOverview.Content(
        modifier: Modifier
    ) = Row(
        modifier = modifier,
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
        fontWeight = FontWeight.Bold,
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
        description = description,
        size = indicatorSize,
        color = color
    ).Content()

    @Composable
    private fun Bloodlust.Icon() = AsyncImage(
        image = icon,
        description = description,
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
            color = color,
            size = dataIconSize
        ).Content()

        Text(text = data.localized())
    }
}