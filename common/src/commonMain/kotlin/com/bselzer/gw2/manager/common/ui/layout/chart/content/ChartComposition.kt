package com.bselzer.gw2.manager.common.ui.layout.chart.content

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.chart.model.ChartData
import com.bselzer.gw2.manager.common.ui.layout.chart.viewmodel.ChartViewModel
import com.bselzer.gw2.manager.common.ui.layout.image.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.image.Content
import com.bselzer.gw2.manager.common.ui.layout.image.ProgressIndication
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.geometry.shape.ArcShape
import com.bselzer.ktx.compose.ui.layout.background.image.BackgroundImage

class ChartComposition(
    model: ChartViewModel,
    private val size: DpSize = DpSize(256.dp, 256.dp)
) : ViewModelComposition<ChartViewModel>(model) {
    @Composable
    override fun ChartViewModel.Content() {
        PieChart()
        ChartDescription()
    }

    @Composable
    private fun ChartViewModel.PieChart() = Box {
        AsyncImage(
            image = chart.background,
            size = size,
        ).Content()

        chart.slices.forEach { slice ->
            AsyncImage(
                image = slice.image,
                size = size,
                description = slice.description,
                color = slice.color
            ).Content(
                modifier = Modifier.clip(ArcShape(slice.startAngle, slice.endAngle)),
            )
        }

        // Add the dividers between the slices.
        chart.slices.map { slice -> slice.startAngle }.forEach { angle ->
            AsyncImage(
                image = chart.divider,
                size = size,
            ).Content(
                progressIndication = ProgressIndication.DISABLED,
                modifier = Modifier.rotate(degrees = angle),
            )
        }
    }

    /**
     * Lays out a description of the chart with its associated data.
     */
    @Composable
    private fun ChartViewModel.ChartDescription() = BackgroundImage(
        painter = relativeBackgroundPainter,
        presenter = relativeBackgroundPresenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = chart.title.localized(),
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )

            // Show the data representing each slice.
            chart.data.forEach { data -> data.ChartData() }
        }
    }

    /**
     * Lays out the data associated with a slice.
     */
    @Composable
    private fun ChartData.ChartData() {
        Text(
            text = owner.localized(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = color,
            textAlign = TextAlign.Center
        )
        Text(
            text = data.localized(),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(3.dp))
    }
}