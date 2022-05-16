package com.bselzer.gw2.manager.common.ui.layout.main.model.module

import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

class WorldModule(
    val image: ImageResource,
    val description: StringDesc,
    val title: StringDesc,
    val subtitle: StringDesc,
    val color: Color
)