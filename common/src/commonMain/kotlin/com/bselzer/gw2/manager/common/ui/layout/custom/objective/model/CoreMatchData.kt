package com.bselzer.gw2.manager.common.ui.layout.custom.objective.model

import com.bselzer.gw2.manager.common.ui.layout.custom.owner.model.Owner
import dev.icerock.moko.resources.desc.StringDesc

interface CoreMatchData {
    val name: StringDesc
    val map: CoreMapData?
    val owner: Owner?
    val flipped: StringDesc?
}