package com.bselzer.gw2.manager.common.ui.layout.custom.progression.content

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
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.common.Content
import com.bselzer.gw2.manager.common.ui.layout.custom.progression.model.Progress
import com.bselzer.gw2.manager.common.ui.layout.custom.progression.model.Progression
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.spacer.Spacer

class ProgressionComposition(
    model: Progression
) : ModelComposition<Progression>(model) {
    @Composable
    override fun Progression.Content(modifier: Modifier) = Column(
        modifier = modifier
    ) {
        Header()
        Progress()
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
            color = color,
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