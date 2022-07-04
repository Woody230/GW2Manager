package com.bselzer.gw2.manager.common.ui.layout.custom.preference.model

import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.interval.WvwIntervalResources
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

data class WvwResources(
    val image: ImageResource,
    val title: StringDesc,
    val interval: WvwIntervalResources
)