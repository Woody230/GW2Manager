package com.bselzer.gw2.manager.common.storage

import com.bselzer.gw2.v2.intl.model.Translation
import com.bselzer.gw2.v2.model.tile.request.TileRequest
import com.bselzer.gw2.v2.model.tile.response.Tile

fun Tile.toTileId(): TileId = TileId(zoom = zoom, x = gridPosition.x, y = gridPosition.y)
fun TileRequest.toTileId(): TileId = TileId(zoom = zoom, x = gridPosition.x, y = gridPosition.y)
fun Translation.toTranslationId(): TranslationId = TranslationId(default = default, language = language)