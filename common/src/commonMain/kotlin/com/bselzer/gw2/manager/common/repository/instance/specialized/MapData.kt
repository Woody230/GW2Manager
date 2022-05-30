package com.bselzer.gw2.manager.common.repository.instance.specialized

import com.bselzer.gw2.v2.tile.model.response.TileGrid

interface MapData {
    /**
     * The constraint the [zoom] must be bound to.
     */
    val zoomRange: IntRange

    /**
     * The zoom level of the map used when creating a request for the [grid].
     */
    val zoom: Int

    /**
     * The size of the map with tile content.
     */
    val grid: TileGrid
}