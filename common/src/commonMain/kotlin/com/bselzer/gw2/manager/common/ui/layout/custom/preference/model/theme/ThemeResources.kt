package com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.theme

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

data class ThemeResources(
    val image: ImageResource,
    val title: StringDesc,
    val subtitle: StringDesc
)