package com.bselzer.gw2.manager.common.ui.layout.host.model.drawer

import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MainConfig
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

data class DrawerComponent(
    val icon: ImageResource,
    val description: StringDesc,
    val configuration: MainConfig
)