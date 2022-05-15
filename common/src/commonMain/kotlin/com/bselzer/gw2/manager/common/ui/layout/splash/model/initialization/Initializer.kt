package com.bselzer.gw2.manager.common.ui.layout.splash.model.initialization

import dev.icerock.moko.resources.desc.StringDesc

data class Initializer(
    val title: StringDesc,
    val subtitle: StringDesc?,
    val block: suspend () -> Unit
)