package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.layout.common.RelativeBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.image.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.image.Content
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.Progress
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.Progression
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMatchStatisticsViewModel
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.column.ColumnPresenter
import com.bselzer.ktx.compose.ui.layout.column.spacedColumnProjector
import com.bselzer.ktx.compose.ui.layout.spacer.Spacer
import com.bselzer.ktx.function.collection.buildArray

class WvwMatchStatisticsComposition(
    model: WvwMatchStatisticsViewModel
) : WvwMatchComposition<WvwMatchStatisticsViewModel, List<Progression>>(model) {
    @Composable
    override fun WvwMatchStatisticsViewModel.Content() = RelativeBackgroundImage(
        modifier = Modifier.fillMaxSize()
    ) {
        BorderlandsContent()
    }

    /**
     * Lays out the information about a particular statistic for each owner.
     */
    @Composable
    override fun List<Progression>.Content() = Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        spacedColumnProjector(
            thickness = 10.dp,
            presenter = ColumnPresenter.CenteredHorizontally
        ).Projection(
            modifier = Modifier.fillMaxWidth(),
            content = buildArray {
                this@Content.forEach { progression ->
                    add {
                        progression.Header()
                        progression.Progress()
                    }
                }
            }
        )
    }

    /**
     * Lays out the header icon and title indicating the type of progress.
     */
    @Composable
    private fun Progression.Header() = Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            image = icon,
            size = DpSize(25.dp, 25.dp)
        ).Content()

        Spacer(width = 5.dp)

        Text(
            text = title.localized(),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h6,
        )
    }

    /**
     * Lays out the progress and counts about a particular statistic for each owner.
     */
    @Composable
    private fun Progression.Progress() = Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        progress.forEach { progress ->
            val percentage = progress.percentage.coerceAtLeast(0.05f)
            progress.Progress(
                modifier = Modifier.weight(percentage)
            )
        }
    }

    /**
     * Lays out the progress and count about a particular statistic for a particular owner.
     */
    @Composable
    private fun Progress.Progress(modifier: Modifier) = Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(3.dp).background(color = color)
        )

        Text(text = amount.toString())
    }
}