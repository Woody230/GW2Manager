package com.bselzer.gw2.manager.common.ui.layout.custom.objective.model

import com.bselzer.gw2.manager.common.ui.layout.custom.objective.viewmodel.ClaimIndicatorViewModel
import dev.icerock.moko.resources.desc.StringDesc

data class Claim(
    val claimedAt: StringDesc,
    val claimedBy: StringDesc,
    val icon: ClaimIndicatorViewModel
)