package com.bselzer.gw2.manager.common.ui.layout.custom.progression.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.progression.model.Progression
import com.bselzer.ktx.compose.ui.layout.column.spacedColumnProjector
import com.bselzer.ktx.function.collection.buildArray

class ProgressionsComposition(
    model: List<Progression>
) : ModelComposition<List<Progression>>(model) {
    @Composable
    override fun List<Progression>.Content(
        modifier: Modifier
    ) = spacedColumnProjector(
        thickness = 10.dp,
    ).Projection(
        modifier = modifier,
        content = buildArray {
            this@Content.forEach { progression ->
                add { ProgressionComposition(progression).Content() }
            }
        }
    )
}