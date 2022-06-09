package com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc
import kotlinx.coroutines.flow.Flow

data class Upgrade(
    val name: StringDesc,
    val link: ImageDesc,
    val description: StringDesc,
    val alpha: Flow<Float>
)