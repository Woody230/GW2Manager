package com.bselzer.gw2.manager.common.repository.instance.generic

import androidx.compose.runtime.mutableStateMapOf
import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.instance.AppRepository
import com.bselzer.gw2.v2.model.continent.Continent
import com.bselzer.gw2.v2.model.continent.ContinentId
import com.bselzer.gw2.v2.model.continent.floor.Floor
import com.bselzer.gw2.v2.model.continent.floor.FloorId
import com.bselzer.gw2.v2.model.map.MapId
import com.bselzer.ktx.kodein.db.operation.getById
import com.bselzer.ktx.kodein.db.transaction.transaction
import com.bselzer.ktx.logging.Logger
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ContinentRepository(
    dependencies: RepositoryDependencies
) : AppRepository(dependencies) {
    private val _continents = mutableStateMapOf<ContinentId, Continent>()
    val continents: Map<ContinentId, Continent> = _continents

    private val _floors = mutableStateMapOf<FloorId, Floor>()
    val floors: Map<FloorId, Floor> = _floors

    private val _maps = mutableStateMapOf<MapId, com.bselzer.gw2.v2.model.map.Map>()
    val maps: Map<MapId, com.bselzer.gw2.v2.model.map.Map> = _maps

    private val configuredContinentId = ContinentId(configuration.wvw.map.continentId)
    private val configuredFloorId = FloorId(configuration.wvw.map.floorId)

    suspend fun updateContinent(mapId: MapId) = database.transaction().use {
        val map = getById(
            id = mapId,
            requestSingle = { clients.gw2.map.map(mapId) },
        )

        _maps[mapId] = map
        coroutineScope {
            launch { updateContinent(map.continentId) }
            launch { updateFloor(map.continentId, map.defaultFloorId) }
        }
    }

    /**
     * Updates the [Continent] and [Floor] associated with the configurable ids for the World vs. World continent.
     *
     * Getting a [MapId] from the match and calling [updateContinent] is the preferred choice instead of this method.
     */
    suspend fun updateWvwContinent() = coroutineScope {
        launch { updateContinent(configuredContinentId) }
        launch { updateFloor(configuredContinentId, configuredFloorId) }
    }

    fun getWvwContinent(mapId: MapId?): Pair<Continent?, Floor?> {
        val continentId = maps[mapId]?.continentId ?: configuredContinentId
        val floorId = maps[mapId]?.defaultFloorId ?: configuredFloorId
        return _continents[continentId] to _floors[floorId]
    }

    private suspend fun updateContinent(continentId: ContinentId) = database.transaction().use {
        Logger.d { "Continent | Updating continent $continentId." }

        val continent = getById(
            id = continentId,
            requestSingle = { clients.gw2.continent.continent(continentId) },
        )

        _continents[continent.id] = continent
    }

    private suspend fun updateFloor(continentId: ContinentId, floorId: FloorId) = database.transaction().use {
        Logger.d { "Continent | Updating floor $floorId in continent $continentId." }

        val floor = getById(
            id = floorId,
            requestSingle = { clients.gw2.continent.floor(continentId, floorId) }
        )

        _floors[floor.id] = floor
    }
}