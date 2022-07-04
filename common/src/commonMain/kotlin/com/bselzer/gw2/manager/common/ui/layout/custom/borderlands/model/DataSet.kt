package com.bselzer.gw2.manager.common.ui.layout.custom.borderlands.model

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc

data class DataSet<T>(
    val title: StringDesc,
    val color: Color?,
    val data: T
)