package com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

data class ObjectiveIcon(
    val link: ImageDesc?,
    val description: StringDesc,
    val color: Color
)