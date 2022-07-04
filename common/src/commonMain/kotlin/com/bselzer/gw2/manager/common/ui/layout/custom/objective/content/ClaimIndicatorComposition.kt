package com.bselzer.gw2.manager.common.ui.layout.custom.objective.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.common.Content
import com.bselzer.gw2.manager.common.ui.layout.common.ProgressIndication
import com.bselzer.gw2.manager.common.ui.layout.custom.objective.viewmodel.ClaimIndicatorViewModel
import com.bselzer.ktx.compose.ui.unit.toDp

class ClaimIndicatorComposition(
    model: ClaimIndicatorViewModel
) : ViewModelComposition<ClaimIndicatorViewModel>(model) {
    @Composable
    override fun ClaimIndicatorViewModel.Content(modifier: Modifier) = AsyncImage(
        image = link,
        size = DpSize(size.toDp(), size.toDp()),
        description = description
    ).Content(
        modifier = modifier,
        progressIndication = ProgressIndication.ENABLED
    )
}