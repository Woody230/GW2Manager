package com.bselzer.gw2.manager.common.ui.layout.custom.statistics.model.overview

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

data class Data(
    val data: StringDesc,
    val icon: ImageDesc,
    val description: StringDesc,
    val color: Color? = null
)