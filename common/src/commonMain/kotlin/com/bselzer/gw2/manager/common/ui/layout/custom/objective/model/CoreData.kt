package com.bselzer.gw2.manager.common.ui.layout.custom.objective.model

import dev.icerock.moko.resources.desc.StringDesc

interface CoreData {
    // Title mapped to the information.
    val pointsPerTick: Pair<StringDesc, StringDesc>
    val pointsPerCapture: Pair<StringDesc, StringDesc>
    val yaks: Pair<StringDesc, StringDesc>?
    val progression: Pair<StringDesc, StringDesc>?
}