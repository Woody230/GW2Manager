package com.bselzer.gw2.manager.common.ui.layout.main.model.match.statistics

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

data class Progression(
    val title: StringDesc,
    val icon: ImageDesc?,
    val progress: List<Progress>,
    val color: Color? = null
)