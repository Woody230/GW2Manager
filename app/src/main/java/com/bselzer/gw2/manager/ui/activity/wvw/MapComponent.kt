package com.bselzer.gw2.manager.ui.activity.wvw

import com.bselzer.library.gw2.v2.model.enumeration.wvw.MapType
import com.bselzer.library.gw2.v2.model.enumeration.wvw.MapType.*
import com.bselzer.library.kotlin.extension.ui.position.Position2D
import com.bselzer.library.kotlin.extension.ui.size.Size2D

class MapComponent(
    val type: MapType
) {
    private companion object {
        val ALPINE_SIZE = Size2D(972, 1376)
        val DESERT_SIZE = Size2D(1196, 1185)
    }

    val size: Size2D = when (type) {
        ETERNAL_BATTLEGROUNDS -> Size2D(1204, 1152)
        RED_BORDERLANDS -> DESERT_SIZE
        BLUE_BORDERLANDS -> ALPINE_SIZE
        GREEN_BORDERLANDS -> ALPINE_SIZE
        EDGE_OF_THE_MISTS -> Size2D(1160, 1136)
    }

    /**
     * The top left position of the component with (0, 0) being the top left corner of the image.
     */
    val position: Position2D = when (type) {
        ETERNAL_BATTLEGROUNDS -> Position2D(1448, 1768)
        RED_BORDERLANDS -> Position2D(1540, 235)
        BLUE_BORDERLANDS -> Position2D(2952, 1000)
        GREEN_BORDERLANDS -> Position2D(148, 1244)
        EDGE_OF_THE_MISTS -> Position2D(312, 36)
    }
}