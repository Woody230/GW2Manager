package com.bselzer.gw2.manager.common.ui.layout.custom.contestedarea.content

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.contestedarea.model.ContestedPointsPerTick
import com.bselzer.ktx.resource.strings.localized

class PointsPerTickComposition(
    pointsPerTick: ContestedPointsPerTick
) : ModelComposition<ContestedPointsPerTick>(pointsPerTick) {
    @Composable
    override fun ContestedPointsPerTick.Content(
        modifier: Modifier
    ) = Text(
        modifier = modifier,
        text = ppt.localized(),
        color = color,
        fontSize = 32.sp
    )
}