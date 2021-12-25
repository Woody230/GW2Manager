package com.bselzer.gw2.manager.android.wvw.state.map.grid

import com.bselzer.gw2.manager.android.wvw.state.map.objective.ObjectiveState

data class GridState(
    val tiles: Collection<Collection<TileState>>,
    val objectives: Collection<ObjectiveState>
)