package com.bselzer.gw2.manager.common.ui.layout.main.model.match

import dev.icerock.moko.resources.desc.StringDesc

data class Progression(
    val title: StringDesc,
    val progress: List<Progress>
)