package com.bselzer.gw2.manager.common.ui.layout.chart.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.chart.model.ChartData
import com.bselzer.gw2.manager.common.ui.layout.chart.model.ChartDataSet
import com.bselzer.gw2.manager.common.ui.layout.chart.viewmodel.ChartDataViewModel
import com.bselzer.gw2.manager.common.ui.layout.common.BorderedCard
import com.bselzer.ktx.compose.resource.strings.localized

class ChartDataComposition(
    model: ChartDataViewModel
) : ViewModelComposition<ChartDataViewModel>(model) {
    @Composable
    override fun ChartDataViewModel.Content(modifier: Modifier) = BorderedCard(
        modifier = Modifier.fillMaxWidth().then(modifier)
    ) {
        dataSet.ChartDescription()
    }

    /**
     * Lays out a description of the chart with its associated data.
     */
    @Composable
    private fun ChartDataSet.ChartDescription() = Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title.localized(),
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            textAlign = TextAlign.Center
        )

        // Show the data representing each slice.
        data.forEach { data -> data.ChartData() }
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