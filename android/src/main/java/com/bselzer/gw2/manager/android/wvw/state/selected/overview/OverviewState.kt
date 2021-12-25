package com.bselzer.gw2.manager.android.wvw.state.selected.overview

data class OverviewState(
    val name: String,
    val map: MapState?,
    val owner: OwnerState?,
    val flipped: String?,
)