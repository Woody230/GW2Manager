package com.bselzer.gw2.manager.common.ui.layout.main.content.map.viewer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.RelativeBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.SelectedLabelViewModel
import com.bselzer.ktx.compose.resource.strings.localized

class SelectedLabelComposition(
    model: SelectedLabelViewModel
) : ModelComposition<SelectedLabelViewModel>(model) {
    @Composable
    override fun SelectedLabelViewModel.Content(modifier: Modifier) = RelativeBackgroundImage(
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(horizontal = 5.dp)) {
            Title()
            Subtitle()
        }
    }

    @Composable
    private fun SelectedLabelViewModel.Title() = Text(text = title.localized(), fontSize = 16.sp, fontWeight = FontWeight.Bold)

    @Composable
    private fun SelectedLabelViewModel.Subtitle() = subtitle?.let { subtitle ->
        Text(text = subtitle.localized(), fontSize = 16.sp)
    }
}