package com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.token

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

data class TokenResources(
    val image: ImageResource,
    val title: StringDesc,
    val subtitle: StringDesc,
    val dialogSubtitle: StringDesc,
    val dialogInput: StringDesc,
    val hyperlink: StringDesc,
    val failure: StringDesc,
)