package com.bselzer.gw2.manager.common.ui.layout.custom.progression.model

import com.bselzer.gw2.manager.common.ui.layout.common.Image
import dev.icerock.moko.resources.desc.StringDesc

data class Progression(
    val title: StringDesc,
    val progress: List<Progress>,
    val image: Image
)