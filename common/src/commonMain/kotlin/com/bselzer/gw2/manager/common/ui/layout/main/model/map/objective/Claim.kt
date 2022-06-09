package com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc

data class Claim(
    val claimedAt: StringDesc,
    val claimedBy: StringDesc,
    val link: ImageDesc,
    val description: StringDesc,
    val size: Int
)