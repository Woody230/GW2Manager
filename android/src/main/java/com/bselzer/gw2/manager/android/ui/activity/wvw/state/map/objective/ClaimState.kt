package com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.objective

data class ClaimState(
    override val enabled: Boolean,
    override val link: String?,
    override val description: String = "Guild Claimed",
    override val width: Int,
    override val height: Int,
) : IndicatorState