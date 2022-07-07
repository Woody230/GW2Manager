package com.bselzer.gw2.manager.common.ui.layout.custom.contestedarea.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.base.ShouldLayoutHorizontally
import com.bselzer.gw2.manager.common.ui.layout.common.Content
import com.bselzer.gw2.manager.common.ui.layout.custom.contestedarea.model.ContestedObjective
import com.bselzer.ktx.resource.strings.localized

class ObjectiveComposition(
    objective: ContestedObjective
) : ModelComposition<ContestedObjective>(objective) {
    @Composable
    override fun ContestedObjective.Content(
        modifier: Modifier
    ) = when (ShouldLayoutHorizontally) {
        true -> HorizontalContent(modifier)
        false -> VerticalContent(modifier)
    }

    @Composable
    private fun ContestedObjective.HorizontalContent(
        modifier: Modifier
    ) = Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon()
        Count()
    }

    @Composable
    private fun ContestedObjective.VerticalContent(
        modifier: Modifier
    ) = Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon()
        Count()
    }

    @Composable
    private fun ContestedObjective.Icon() = image.Content(size = DpSize(50.dp, 50.dp))

    @Composable
    private fun ContestedObjective.Count() = Text(text = count.localized())
}