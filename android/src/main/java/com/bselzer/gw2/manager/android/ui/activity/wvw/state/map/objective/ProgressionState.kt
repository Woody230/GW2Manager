package com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.objective

data class ProgressionState(
    override val enabled: Boolean,
    override val link: String?,
    override val description: String = "Upgraded",
    override val width: Int,
    override val height: Int,
) : IndicatorState