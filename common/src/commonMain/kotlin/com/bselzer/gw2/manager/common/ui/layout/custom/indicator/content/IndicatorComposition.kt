package com.bselzer.gw2.manager.common.ui.layout.custom.indicator.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.Content
import com.bselzer.gw2.manager.common.ui.layout.common.Image
import com.bselzer.gw2.manager.common.ui.layout.common.ProgressIndication

class IndicatorComposition(
    model: Image
) : ModelComposition<Image>(model) {
    @Composable
    override fun Image.Content(modifier: Modifier) = Content(
        progressIndication = ProgressIndication.DISABLED,
        modifier = modifier,
        size = DpSize(16.dp, 16.dp)
    )
}