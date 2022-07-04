package com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.interval

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import kotlin.time.DurationUnit

data class WvwIntervalResources(
    val image: ImageResource,
    val title: StringDesc,
    val subtitle: StringDesc,
    val label: (DurationUnit) -> StringDesc
)