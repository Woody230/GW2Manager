package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.layout.common.RelativeBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.custom.borderlands.content.BorderlandsComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.statistics.content.ProgressionsComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.statistics.model.data.Progression
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMatchStatisticsViewModel

class WvwMatchStatisticsComposition(
    model: WvwMatchStatisticsViewModel
) : MainChildComposition<WvwMatchStatisticsViewModel>(model), BorderlandsComposition<List<Progression>> {
    @Composable
    override fun WvwMatchStatisticsViewModel.Content(modifier: Modifier) = RelativeBackgroundImage(
        modifier = Modifier.fillMaxSize().then(modifier)
    ) {
        BorderlandsContent()
    }

    /**
     * Lays out the information about a particular statistic for each owner.
     */
    @Composable
    override fun ColumnScope.Content(index: Int, data: List<Progression>) = Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        ProgressionsComposition(data).Content(modifier = Modifier.fillMaxWidth())
    }
}