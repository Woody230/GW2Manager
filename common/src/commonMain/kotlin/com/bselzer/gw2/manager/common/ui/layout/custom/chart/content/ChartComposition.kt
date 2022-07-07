package com.bselzer.gw2.manager.common.ui.layout.custom.chart.content

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
import com.bselzer.gw2.manager.common.ui.layout.common.Content
import com.bselzer.gw2.manager.common.ui.layout.common.ProgressIndication
import com.bselzer.gw2.manager.common.ui.layout.custom.chart.model.Chart
import com.bselzer.gw2.manager.common.ui.layout.custom.chart.model.ChartSlice
import com.bselzer.gw2.manager.common.ui.layout.custom.chart.viewmodel.ChartViewModel
import com.bselzer.ktx.compose.ui.ApplicationSize
import com.bselzer.ktx.compose.ui.geometry.shape.ArcShape

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
    override fun ChartViewModel.Content(modifier: Modifier) = chart.PieChart(modifier)

    @Composable
    private fun Chart.PieChart(modifier: Modifier) = Box(
        modifier = modifier
    ) {
        // Add the background behind everything else.
        background.Content(size = size)

        // Overlay the slices over the background.
        slices.forEach { slice -> slice.Image() }

        // Add the dividers between the slices.
        slices.map { slice -> slice.startAngle }.forEach { angle -> Divider(angle) }
    }

    @Composable
    private fun ChartSlice.Image() = image.Content(
        size = size,
        modifier = Modifier.clip(
            shape = ArcShape(startAngle, endAngle)
        ),
    )

    @Composable
    private fun Chart.Divider(angle: Float) = divider.Content(
        progressIndication = ProgressIndication.DISABLED,
        modifier = Modifier.rotate(degrees = angle),
        size = size
    )
}