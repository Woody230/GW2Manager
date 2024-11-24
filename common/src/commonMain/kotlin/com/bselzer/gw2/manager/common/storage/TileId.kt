package com.bselzer.gw2.manager.common.storage

import kotlinx.serialization.Serializable

@Serializable
data class TileId(
    val zoom: Int,
    val x: Int,
    val y: Int
)