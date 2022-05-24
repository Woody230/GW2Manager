package com.bselzer.gw2.manager.common.ui.layout.main.model.settings

import androidx.compose.ui.text.intl.Locale
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

data class LanguageResources(
    val image: ImageResource,
    val title: StringDesc,
    val subtitle: StringDesc,
    val getLabel: (Locale) -> StringDesc
)