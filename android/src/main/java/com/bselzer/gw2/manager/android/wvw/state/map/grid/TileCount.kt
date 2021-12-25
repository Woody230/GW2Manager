package com.bselzer.gw2.manager.android.wvw.state.map.grid

/**
 * The number of tiles with content and the total number of tiles for the current zoom level.
 */
data class TileCount(val contentSize: Int, val gridSize: Int) {
    val isEmpty: Boolean = gridSize == 0 || contentSize == 0
    val hasAllContent: Boolean = contentSize >= gridSize
}