package com.bselzer.gw2.manager.common.ui.layout.main.model.settings

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

data class WvwResources(
    val image: ImageResource,
    val title: StringDesc,
    val interval: WvwIntervalResources
)