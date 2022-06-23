package com.bselzer.gw2.manager.common.ui.layout.main.model.match

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc

data class DataSet(
    val title: StringDesc,
    val color: Color,
    val progressions: Collection<Progression>
)