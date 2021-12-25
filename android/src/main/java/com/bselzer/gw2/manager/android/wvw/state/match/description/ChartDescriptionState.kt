package com.bselzer.gw2.manager.android.wvw.state.match.description

data class ChartDescriptionState(
    val title: ChartTitleState,
    val data: Collection<ChartDataState>,
)