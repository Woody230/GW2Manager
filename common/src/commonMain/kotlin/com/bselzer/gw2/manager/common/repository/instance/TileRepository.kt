package com.bselzer.gw2.manager.common.repository.instance

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.base.AppRepository
import com.bselzer.gw2.v2.model.continent.Continent
import com.bselzer.gw2.v2.model.continent.floor.Floor
import com.bselzer.gw2.v2.tile.cache.metadata.id
import com.bselzer.gw2.v2.tile.model.request.TileGridRequest
import com.bselzer.gw2.v2.tile.model.response.Tile
import com.bselzer.gw2.v2.tile.model.response.TileGrid
import com.bselzer.ktx.kodein.db.transaction.transaction
import com.bselzer.ktx.logging.Logger
import org.kodein.db.getById

class TileRepository(
    dependencies: RepositoryDependencies
) : AppRepository(dependencies) {
    /**
     * Creates the grid with tile content populated for the given [zoom] level.
     */
    suspend fun getGrid(continent: Continent, floor: Floor, zoom: Int): TileGrid {
        val request = getGridRequest(continent, floor, zoom)
        return TileGrid(request, getTiles(request))
    }

    /**
     * Gets the tiles associated with the tile requests on the [gridRequest].
     */
    suspend fun getTiles(gridRequest: TileGridRequest): List<Tile> = database.transaction().use {
        val missing = gridRequest.tileRequests.filter { tileRequest ->
            getById<Tile>(tileRequest.id()) == null
        }

        clients.tile.tilesAsync(missing).map { deferred ->
            deferred.await().also { tile -> put(tile) }
        }

        gridRequest.tileRequests.mapNotNull { tileRequest ->
            getById(tileRequest.id())
        }
    }

    /**
     * Creates the request for the grid, which may be bounded to a configured size.
     */
    fun getGridRequest(continent: Continent, floor: Floor, zoom: Int): TileGridRequest = clients.tile.requestGrid(
        continent = continent,
        floor = floor,
        zoom = zoom
    ).let { request ->
        if (configuration.wvw.map.isBounded) {
            // Cut off unneeded tiles.
            val bound = configuration.wvw.map.levels.firstOrNull { level -> level.zoom == zoom }?.bound
            if (bound != null) {
                return request.bounded(startX = bound.startX, startY = bound.startY, endX = bound.endX, endY = bound.endY)
            } else {
                Logger.w { "Unable to create a bounded request for zoom level $zoom" }
            }
        }

        return request
    }
}
