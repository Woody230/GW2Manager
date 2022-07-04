package com.bselzer.gw2.manager.common.ui.layout.custom.claim.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.Content
import com.bselzer.gw2.manager.common.ui.layout.common.ImageImpl
import com.bselzer.gw2.manager.common.ui.layout.common.ProgressIndication
import com.bselzer.gw2.manager.common.ui.layout.custom.claim.viewmodel.ClaimImageViewModel
import com.bselzer.ktx.compose.ui.unit.toDp

class ClaimImageComposition(
    model: ClaimImageViewModel
) : ViewModelComposition<ClaimImageViewModel>(model) {
    @Composable
    override fun ClaimImageViewModel.Content(modifier: Modifier) = ImageImpl(
        image = link,
        description = description
    ).Content(
        modifier = modifier,
        size = DpSize(size.toDp(), size.toDp()),
        progressIndication = ProgressIndication.ENABLED
    )
}