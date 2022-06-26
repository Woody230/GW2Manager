package com.bselzer.gw2.manager.common.ui.layout.contestedarea.model

import dev.icerock.moko.resources.desc.StringDesc

data class ContestedObjectives(
    val ppt: StringDesc,
    val objectives: List<ContestedObjective>
)