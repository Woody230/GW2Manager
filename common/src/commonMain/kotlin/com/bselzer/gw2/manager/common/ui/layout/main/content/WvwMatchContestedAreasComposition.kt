package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.layout.borderlands.content.BorderlandsComposition
import com.bselzer.gw2.manager.common.ui.layout.chart.content.ChartComposition
import com.bselzer.gw2.manager.common.ui.layout.common.RelativeBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.content.ContestedAreasComposition
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.viewmodel.ContestedAreasViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMatchContestedAreasViewModel
import com.bselzer.gw2.v2.model.extension.wvw.count.ObjectiveOwnerCount
import com.bselzer.ktx.compose.ui.layout.ApplicationSize

class WvwMatchContestedAreasComposition(
    model: WvwMatchContestedAreasViewModel
) : MainChildComposition<WvwMatchContestedAreasViewModel>(model),
    BorderlandsComposition<WvwMatchContestedAreasViewModel, ObjectiveOwnerCount>,
    ContestedAreasComposition<ContestedAreasViewModel> {
    @Composable
    override fun WvwMatchContestedAreasViewModel.Content(modifier: Modifier) = RelativeBackgroundImage(
        modifier = Modifier.fillMaxSize().then(modifier)
    ) {
        BorderlandsContent()
    }

    @Composable
    override fun ColumnScope.Content(index: Int, data: ObjectiveOwnerCount) = with(model) {
        val chart: @Composable () -> Unit = {
            val model = charts.getOrNull(index)
            model?.let { ChartComposition(model).Content() }
        }

        val contestedAreas: @Composable () -> Unit = {
            val model = data.toContestedAreasModel()
            model.ContestedAreasContent()
        }

        // Depending on rotation or window size, adjust whether to display horizontally or vertically.
        if (ApplicationSize.current.width > ApplicationSize.current.height) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                chart()
                contestedAreas()
            }
        } else {
            chart()
            contestedAreas()
        }
    }
}