package com.bselzer.gw2.manager.common.repository.instance.specialized

import androidx.compose.runtime.mutableStateOf
import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.instance.generic.GenericRepositories
import com.bselzer.gw2.v2.model.continent.Continent
import com.bselzer.gw2.v2.model.continent.floor.Floor
import com.bselzer.gw2.v2.model.map.MapId
import com.bselzer.gw2.v2.tile.model.response.TileGrid
import com.bselzer.ktx.logging.Logger
import kotlin.math.max
import kotlin.math.min

class MapRepository(
    dependencies: RepositoryDependencies,
    repositories: GenericRepositories
) : SpecializedRepository(dependencies, repositories), MapData {
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
     * Updates the grid with the new [zoom] level for the continent and floor associated with the map with id [mapId].
     */
    suspend fun updateZoom(zoom: Int, mapId: MapId?) {
        // Must keep the zoom bounded within the configured range.
        val bounded = max(zoomRange.first, min(zoomRange.last, zoom))
        _zoom.value = bounded

        updateGrid(zoom, mapId)
    }

    /**
     * Updates the grid for the continent and floor associated with the map with id [mapId].
     */
    private suspend fun updateGrid(zoom: Int, mapId: MapId?) {
        // Since tiles are expensive, only update the grid when designated.
        if (!refreshGrid) {
            Logger.d { "Map | Grid | Skipping refresh." }
            return
        }

        Logger.d { "Map | Grid | Refreshing with zoom level $zoom and map $mapId." }

        val (continent, floor) = repositories.continent.getWvwContinent(mapId)
        if (continent != null && floor != null) {
            repositories.tile.updateGrid(continent, floor, zoom)
        }
    }
}