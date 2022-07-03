package com.bselzer.gw2.manager.common.ui.layout.statistics.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.layout.statistics.viewmodel.OwnerOverviewsViewModel

interface OwnerOverviewsComposition {
    @Composable
    fun OwnerOverviewsViewModel.OwnerOverviewsContent(
        modifier: Modifier = Modifier
    ) = Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        overviews.forEach { overview ->
            OwnerOverviewComposition(overview).Content()
        }
    }
}