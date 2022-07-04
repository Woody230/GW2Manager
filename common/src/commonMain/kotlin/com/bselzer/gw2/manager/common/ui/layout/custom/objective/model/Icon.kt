package com.bselzer.gw2.manager.common.ui.layout.custom.objective.model

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

data class Icon(
    val link: ImageDesc?,
    val description: StringDesc,
    val color: Color
)