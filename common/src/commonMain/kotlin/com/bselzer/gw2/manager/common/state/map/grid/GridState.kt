package com.bselzer.gw2.manager.common.state.map.grid

import com.bselzer.gw2.manager.common.state.map.objective.ObjectiveState

data class GridState(
    val tiles: Collection<Collection<TileState>>,
    val objectives: Collection<ObjectiveState>
)