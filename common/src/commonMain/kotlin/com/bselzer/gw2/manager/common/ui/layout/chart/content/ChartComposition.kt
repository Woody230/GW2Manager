package com.bselzer.gw2.manager.common.ui.layout.chart.content

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.chart.model.Chart
import com.bselzer.gw2.manager.common.ui.layout.chart.model.ChartSlice
import com.bselzer.gw2.manager.common.ui.layout.chart.viewmodel.ChartViewModel
import com.bselzer.gw2.manager.common.ui.layout.image.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.image.Content
import com.bselzer.gw2.manager.common.ui.layout.image.ProgressIndication
import com.bselzer.ktx.compose.ui.geometry.shape.ArcShape
import com.bselzer.ktx.compose.ui.layout.ApplicationSize

class ChartComposition(
    model: ChartViewModel,
) : ViewModelComposition<ChartViewModel>(model) {
    private companion object {
        val size: DpSize
            @Composable
            get() {
                val preferred = DpSize(256.dp, 256.dp)
                val applicationSize = ApplicationSize.current

                val fillPercentage = 0.5f
                val width = preferred.width.coerceAtMost(applicationSize.width * fillPercentage)
                val height = preferred.height.coerceAtMost(applicationSize.height * fillPercentage)
                val size = min(width, height)
                return DpSize(size, size)
            }
    }

    @Composable
    override fun ChartViewModel.Content() = chart.PieChart()

    @Composable
    private fun Chart.PieChart() = Box {
        // Add the background behind everything else.
        Background()

        // Overlay the slices over the background.
        slices.forEach { slice -> slice.Image() }

        // Add the dividers between the slices.
        slices.map { slice -> slice.startAngle }.forEach { angle -> Divider(angle) }
    }

    @Composable
    private fun Chart.Background() = AsyncImage(
        image = background,
        size = size,
    ).Content()

    @Composable
    private fun ChartSlice.Image() = AsyncImage(
        image = image,
        size = size,
        description = description,
        color = color
    ).Content(
        modifier = Modifier.clip(
            shape = ArcShape(startAngle, endAngle)
        ),
    )

    @Composable
    private fun Chart.Divider(angle: Float) = AsyncImage(
        image = divider,
        size = size,
    ).Content(
        progressIndication = ProgressIndication.DISABLED,
        modifier = Modifier.rotate(degrees = angle),
    )
}