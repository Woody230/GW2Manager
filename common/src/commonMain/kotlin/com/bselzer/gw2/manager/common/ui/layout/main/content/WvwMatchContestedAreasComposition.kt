package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.base.ShouldLayoutHorizontally
import com.bselzer.gw2.manager.common.ui.layout.common.RelativeBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.custom.borderlands.content.BorderlandsComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.chart.content.ChartComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.contestedarea.content.ContestedAreasComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMatchContestedAreasViewModel
import com.bselzer.gw2.v2.model.extension.wvw.count.ObjectiveOwnerCount
import com.bselzer.ktx.compose.ui.layout.spacer.Spacer

class WvwMatchContestedAreasComposition(
    model: WvwMatchContestedAreasViewModel
) : MainChildComposition<WvwMatchContestedAreasViewModel>(model),
    BorderlandsComposition<ObjectiveOwnerCount>,
    ContestedAreasComposition {
    @Composable
    override fun WvwMatchContestedAreasViewModel.Content(modifier: Modifier) = RelativeBackgroundImage(
        modifier = Modifier.fillMaxSize().then(modifier),
    ) {
        BorderlandsContent()
    }

    @Composable
    override fun ColumnScope.Content(index: Int, data: ObjectiveOwnerCount) = with(model) {
        if (ShouldLayoutHorizontally) {
            HorizontalContent(index, data)
        } else {
            VerticalContent(index, data)
        }
    }

    @Composable
    private fun WvwMatchContestedAreasViewModel.HorizontalContent(
        index: Int, data: ObjectiveOwnerCount
    ) = Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Chart(index)
        Spacer(width = 20.dp)
        ContestedAreas(data)
    }

    @Composable
    private fun WvwMatchContestedAreasViewModel.VerticalContent(
        index: Int, data: ObjectiveOwnerCount
    ) = Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Chart(index)
        ContestedAreas(data)
    }

    @Composable
    private fun WvwMatchContestedAreasViewModel.Chart(index: Int) {
        val model = charts.getOrNull(index)
        model?.let { ChartComposition(model).Content() }
    }

    @Composable
    private fun WvwMatchContestedAreasViewModel.ContestedAreas(
        data: ObjectiveOwnerCount
    ) {
        val model = data.toContestedAreasModel()
        model.ContestedAreasContent()
    }
}