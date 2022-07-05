package com.bselzer.gw2.manager.common.ui.layout.main.content.map.viewer

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.RelativeBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.MapLabelViewModel
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.unit.toDp

class MapLabelComposition(
    model: MapLabelViewModel
) : ModelComposition<MapLabelViewModel>(model) {
    @Composable
    override fun MapLabelViewModel.Content(modifier: Modifier) = RelativeBackgroundImage(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter,
    ) {
        Text(
            // Only span the size of the map at most.
            modifier = Modifier.widthIn(max = width.toDp()),
            text = description.localized(),
            fontWeight = FontWeight.ExtraBold,
            color = color,
        )
    }
}