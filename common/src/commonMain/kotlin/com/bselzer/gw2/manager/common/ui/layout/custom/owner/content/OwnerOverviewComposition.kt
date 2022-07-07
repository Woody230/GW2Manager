package com.bselzer.gw2.manager.common.ui.layout.custom.owner.content

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
import com.bselzer.gw2.manager.common.ui.layout.common.Content
import com.bselzer.gw2.manager.common.ui.layout.custom.owner.model.Data
import com.bselzer.gw2.manager.common.ui.layout.custom.owner.model.OwnerOverview
import com.bselzer.ktx.compose.ui.layout.row.spacedRowProjector
import com.bselzer.ktx.function.collection.buildArray
import com.bselzer.ktx.resource.strings.localized

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
        // TODO constraint layout
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
            add { skirmishWarScore.DataPoint() }
            add { totalWarScore.DataPoint() }
        }
    )

    @Composable
    private fun OwnerOverview.Icons() = spacedRowProjector(
        thickness = 5.dp
    ).Projection(
        content = buildArray {
            home?.let { home ->
                add { home.Content(size = indicatorSize) }
            }

            bloodlusts.forEach { bloodlust ->
                add { bloodlust.Content(size = indicatorSize) }
            }
        }
    )

    @Composable
    private fun Data.DataPoint() = Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        image.Content(size = dataIconSize)
        Text(text = data.localized())
    }
}