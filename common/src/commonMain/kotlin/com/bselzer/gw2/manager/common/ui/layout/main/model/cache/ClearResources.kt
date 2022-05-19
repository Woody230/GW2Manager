package com.bselzer.gw2.manager.common.ui.layout.main.model.cache

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

data class ClearResources(
    val type: ClearType,
    val image: ImageResource,
    val title: StringDesc,
    val subtitle: StringDesc,
)