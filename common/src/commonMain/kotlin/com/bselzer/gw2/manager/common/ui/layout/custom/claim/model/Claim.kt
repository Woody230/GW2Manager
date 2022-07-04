package com.bselzer.gw2.manager.common.ui.layout.custom.claim.model

import com.bselzer.gw2.manager.common.ui.layout.custom.claim.viewmodel.ClaimImageViewModel
import dev.icerock.moko.resources.desc.StringDesc

data class Claim(
    val claimedAt: StringDesc,
    val claimedBy: StringDesc,
    val icon: ClaimImageViewModel
)