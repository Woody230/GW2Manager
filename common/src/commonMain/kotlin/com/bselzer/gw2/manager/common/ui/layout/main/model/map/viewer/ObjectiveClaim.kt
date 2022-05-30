package com.bselzer.gw2.manager.common.ui.layout.main.model.map.viewer

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

data class ObjectiveClaim(
    val enabled: Boolean,
    val link: ImageDesc?,
    val description: StringDesc,
    val width: Int,
    val height: Int,
)