package com.bselzer.gw2.manager.common.ui.layout.custom.contestedarea.model

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

data class ContestedObjective(
    val link: ImageDesc?,
    val count: StringDesc,
    val color: Color?,
    val description: StringDesc,
)