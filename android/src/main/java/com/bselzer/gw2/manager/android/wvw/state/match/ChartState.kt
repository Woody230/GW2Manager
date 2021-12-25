package com.bselzer.gw2.manager.android.wvw.state.match

import com.bselzer.gw2.manager.android.wvw.state.match.description.ChartDescriptionState
import com.bselzer.gw2.manager.android.wvw.state.match.pie.ChartBackgroundState
import com.bselzer.gw2.manager.android.wvw.state.match.pie.ChartDividerState
import com.bselzer.gw2.manager.android.wvw.state.match.pie.ChartSliceState

data class ChartState(
    val description: ChartDescriptionState,
    val divider: ChartDividerState,
    val background: ChartBackgroundState,
    val slices: Collection<ChartSliceState>,
)