package com.bselzer.gw2.manager.common.ui.layout.main.model.settings

import com.bselzer.ktx.compose.ui.graphics.color.Hex
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

data class ColorResources(
    val image: ImageResource,
    val title: StringDesc,
    val subtitle: Hex,
    val dialogInput: StringDesc,
    val dialogSubtitle: StringDesc,
    val failure: StringDesc,
    val hasValidInput: Boolean
)