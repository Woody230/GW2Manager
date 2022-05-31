package com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective

import dev.icerock.moko.resources.desc.StringDesc

data class Claim(
    val claimedAt: StringDesc,
    val claimedBy: StringDesc,
    val icon: Icon,
)