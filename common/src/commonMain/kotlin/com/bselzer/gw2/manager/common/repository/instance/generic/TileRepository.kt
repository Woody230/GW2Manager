package com.bselzer.gw2.manager.common.repository.instance.generic

import androidx.compose.runtime.mutableStateMapOf
import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.dependency.Singleton
import com.bselzer.gw2.v2.model.continent.Continent
import com.bselzer.gw2.v2.model.continent.floor.Floor
import com.bselzer.gw2.v2.tile.cache.metadata.id
import com.bselzer.gw2.v2.tile.model.request.TileGridRequest
import com.bselzer.gw2.v2.tile.model.request.TileRequest
import com.bselzer.gw2.v2.tile.model.response.Tile
import com.bselzer.gw2.v2.tile.model.response.TileGrid
import com.bselzer.ktx.coroutine.sync.LockByKey
import com.bselzer.ktx.kodein.db.operation.getById
import com.bselzer.ktx.kodein.db.transaction.transaction
import com.bselzer.ktx.logging.Logger
import me.tatarka.inject.annotations.Inject
import org.kodein.db.Value
import org.kodein.db.getById

@Singleton
@Inject
class TileRepository(
    dependencies: RepositoryDependencies
) : RepositoryDependencies by dependencies {
    private val lock = LockByKey<Value>()

    /**
     * The zoom level mapped to the request for the grid.
     */
    private val _gridRequests = mutableStateMapOf<Int, TileGridRequest>()
    val gridRequests: Map<Int, TileGridRequest> = _gridRequests

    /**
     * The tile mapped to its content.
     *
     * The mapped content must be used as the tile will not have its content populated.
     */
    private val _tileContent = mutableStateMapOf<Tile, ByteArray>()
    val tileContent: Map<Tile, ByteArray> = _tileContent

    /**
     * Release the tiles not associated with the given [zoom] level.
     */
    fun release(zoom: Int) {
        _tileContent.keys.filter { tile -> tile.zoom != zoom }.forEach { tile -> _tileContent.remove(tile) }
    }

    /**
     * Creates the grid with tile content populated for the given [zoom] level.
     */
    fun getGrid(zoom: Int): TileGrid {
        val gridRequest = gridRequests[zoom] ?: return TileGrid()
        val tiles = gridRequest.tileRequests.map { tileRequest ->
            val tile = Tile(tileRequest)
            val content = tileContent[tile] ?: byteArrayOf()
            tile.copy(content = content)
        }

        return TileGrid(request = gridRequest, tiles = tiles)
    }

    /**
     * Updates the tile associated with the [TileRequest].
     */
    suspend fun updateTile(tileRequest: TileRequest) = database.transaction().use {
        getById(
            id = tileRequest.id(),
            requestSingle = {
                lock.withLock(tileRequest.id()) {
                    clients.tile.tile(tileRequest)
                }
            },
            writeFilter = { tile -> tile.content.isNotEmpty() }
        )
    }

    /**
     * Updates the [TileGridRequest] for the given [zoom] level and the tiles associated with it.
     */
    suspend fun updateGrid(continent: Continent, floor: Floor, zoom: Int) {
        Logger.d { "Grid | Updating grid at zoom level $zoom for continent ${continent.id} and floor ${floor.id}." }

        val gridRequest = getGridRequest(continent, floor, zoom)
        _gridRequests[zoom] = gridRequest
        updateTiles(gridRequest)
    }

    /**
     * Gets the tiles associated with the tile requests on the [gridRequest].
     */
    private suspend fun updateTiles(gridRequest: TileGridRequest) = database.transaction().use {
        Logger.d { "Grid | Updating ${gridRequest.tileRequests.size} tiles." }

        val missing = gridRequest.tileRequests.filter { tileRequest ->
            getById<Tile>(tileRequest.id()) == null
        }

        clients.tile.tilesAsync(missing).map { deferred ->
            deferred.await().also { tile ->
                if (tile.content.isNotEmpty()) {
                    put(tile)
                }

                // Immediately put the result in case we are awaiting many other tiles such as on initial load.
                _tileContent[tile] = tile.content
            }
        }

        gridRequest.tileRequests.mapNotNull { tileRequest ->
            getById<Tile>(tileRequest.id())
        }.forEach { tile ->
            _tileContent[tile] = tile.content
        }
    }

    /**
     * Creates the request for the grid, which may be bounded to a configured size.
     */
    private fun getGridRequest(continent: Continent, floor: Floor, zoom: Int): TileGridRequest = clients.tile.requestGrid(
        continent = continent,
        floor = floor,
        zoom = zoom
    ).let { request ->
        if (configuration.wvw.map.isBounded) {
            // Cut off unneeded tiles.
            val level = configuration.wvw.map.levels.firstOrNull { level -> level.zoom == zoom }
            if (level != null) {
                return request.bounded(startX = level.startX, startY = level.startY, endX = level.endX, endY = level.endY)
            } else {
                Logger.w { "Unable to create a bounded request for zoom level $zoom" }
            }
        }

        return request
    }
}
