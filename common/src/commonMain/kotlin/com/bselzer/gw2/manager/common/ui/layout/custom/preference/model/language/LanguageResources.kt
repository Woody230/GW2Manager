package com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.language

import com.bselzer.ktx.intl.Locale
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

data class LanguageResources(
    val image: ImageResource,
    val title: StringDesc,
    val subtitle: StringDesc,
    val getLabel: (Locale) -> StringDesc
)