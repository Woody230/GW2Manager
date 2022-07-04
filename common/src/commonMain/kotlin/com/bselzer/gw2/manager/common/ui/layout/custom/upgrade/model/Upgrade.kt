package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc
import kotlinx.coroutines.flow.Flow

interface Upgrade {
    val name: StringDesc
    val link: ImageDesc
    val description: StringDesc
    val alpha: Flow<Float>
}