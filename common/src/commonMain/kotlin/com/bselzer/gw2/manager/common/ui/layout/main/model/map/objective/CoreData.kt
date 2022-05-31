package com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective

import dev.icerock.moko.resources.desc.StringDesc

data class CoreData(
    // Title mapped to the information.
    val pointsPerTick: Pair<StringDesc, StringDesc>,
    val pointsPerCapture: Pair<StringDesc, StringDesc>,
    val yaks: Pair<StringDesc, StringDesc>?,
    val upgrade: Pair<StringDesc, StringDesc>?
)