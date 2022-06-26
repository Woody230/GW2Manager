package com.bselzer.gw2.manager.common.ui.layout.contestedarea.model

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc

data class ContestedObjectives(
    val ppt: StringDesc,
    val color: Color,
    val objectives: List<ContestedObjective>
)