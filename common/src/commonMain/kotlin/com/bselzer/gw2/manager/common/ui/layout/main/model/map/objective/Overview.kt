package com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective

import dev.icerock.moko.resources.desc.StringDesc

data class Overview(
    val name: StringDesc,
    val map: MapInfo?,
    val owner: Owner?,
    val flipped: StringDesc?,
)