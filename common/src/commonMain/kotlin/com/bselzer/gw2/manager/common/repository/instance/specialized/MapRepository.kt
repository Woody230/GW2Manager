package com.bselzer.gw2.manager.common.repository.instance.specialized

import androidx.compose.runtime.mutableStateOf
import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.dependency.Singleton
import com.bselzer.gw2.manager.common.repository.data.specific.MapData
import com.bselzer.gw2.manager.common.repository.instance.generic.ContinentRepository
import com.bselzer.gw2.manager.common.repository.instance.generic.TileRepository
import com.bselzer.gw2.v2.model.continent.Continent
import com.bselzer.gw2.v2.model.continent.floor.Floor
import com.bselzer.gw2.v2.model.map.MapId
import com.bselzer.gw2.v2.tile.cache.metadata.id
import com.bselzer.gw2.v2.tile.model.response.Tile
import com.bselzer.gw2.v2.tile.model.response.TileGrid
import com.bselzer.ktx.logging.Logger
import me.tatarka.inject.annotations.Inject

@Singleton
@Inject
class MapRepository(
    dependencies: RepositoryDependencies,
    private val repositories: Repositories
) : RepositoryDependencies by dependencies, MapData {
    @Singleton
    @Inject
    data class Repositories(
        val continent: ContinentRepository,
        val tile: TileRepository
    )

    override val zoomRange: IntRange = IntRange(start = configuration.wvw.map.zoom.min, endInclusive = configuration.wvw.map.zoom.max)
    private val _zoom = mutableStateOf(configuration.wvw.map.zoom.default)
    override val zoom: Int
        get() = _zoom.value

    /**
     * The [TileGrid] for the current zoom level.
     */
    override val grid: TileGrid
        get() = repositories.tile.getGrid(zoom)

    /**
     * Whether the grid should be refreshed when the continent is updated.
     */
    private var _refreshGrid: Boolean = false
    override var refreshGrid: Boolean
        get() = synchronized(this) { _refreshGrid }
        set(value) {
            synchronized(this) {
                Logger.d { "Map | Grid | Setting refresh to $value." }
                _refreshGrid = value
            }
        }

    /**
     * Gets the [Continent] for the given [mapId].
     */
    fun getContinent(mapId: MapId?): Continent? = repositories.continent.getWvwContinent(mapId).first

    /**
     * Gets the [Floor] for the given [mapId].
     */
    fun getFloor(mapId: MapId?): Floor? = repositories.continent.getWvwContinent(mapId).second

    /**
     * Gets the map for the given [mapId].
     */
    fun getMap(mapId: MapId) = repositories.continent.maps[mapId]

    /**
     * Updates the grid with the current zoom level for the continent and floor associated with the map with id [mapId].
     */
    suspend fun updateContinent(mapId: MapId?) {
        if (mapId == null || mapId.isDefault) {
            // Default to what is in the config to determine the correct continent.
            repositories.continent.updateWvwContinent()
        } else {
            // Update the associated continent from the map.
            repositories.continent.updateContinent(mapId)
        }

        updateGrid(zoom, mapId)
    }

    /**
     * Requests an update for the contents associated with the [tile].
     */
    override suspend fun request(tile: Tile): Tile {
        val gridRequest = repositories.tile.gridRequests[zoom] ?: return tile
        val tileRequest = gridRequest.tileRequests.firstOrNull { tileRequest -> tileRequest.id() == tile.id() } ?: return tile
        return repositories.tile.updateTile(tileRequest)
    }

    /**
     * Updates the grid with the new [zoom] level for the continent and floor associated with the map with id [mapId].
     */
    suspend fun updateZoom(zoom: Int, mapId: MapId?) {
        // Must keep the zoom bounded within the configured range.
        _zoom.value = zoom.coerceIn(zoomRange)

        updateGrid(zoom, mapId)
    }

    /**
     * Updates the grid for the continent and floor associated with the map with id [mapId] for the current zoom level.
     */
    suspend fun updateGrid(mapId: MapId?) = updateGrid(zoom, mapId)

    /**
     * Updates the grid for the continent and floor associated with the map with id [mapId].
     */
    private suspend fun updateGrid(zoom: Int, mapId: MapId?) {
        val (continent, floor) = repositories.continent.getWvwContinent(mapId)
        if (continent != null && floor != null) {
            Logger.d { "Map | Grid | Refreshing with zoom level zoom and map $mapId." }
            val request = repositories.tile.updateGridRequest(continent, floor, zoom)

            // Since tiles are expensive, only update the grid when designated.
            if (refreshGrid) {
                repositories.tile.updateTiles(request)
            }
        }

        // Release the tiles not being used to be garbage collected.
        repositories.tile.release(zoom)
    }
}