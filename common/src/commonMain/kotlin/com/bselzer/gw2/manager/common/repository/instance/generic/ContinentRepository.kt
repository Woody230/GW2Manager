package com.bselzer.gw2.manager.common.repository.instance.generic

import androidx.compose.runtime.mutableStateMapOf
import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.dependency.Singleton
import com.bselzer.gw2.v2.intl.translation.Gw2Translators
import com.bselzer.gw2.v2.model.continent.Continent
import com.bselzer.gw2.v2.model.continent.ContinentId
import com.bselzer.gw2.v2.model.continent.floor.Floor
import com.bselzer.gw2.v2.model.continent.floor.FloorId
import com.bselzer.gw2.v2.model.map.MapId
import com.bselzer.ktx.function.collection.putInto
import com.bselzer.ktx.kodein.db.operation.getById
import com.bselzer.ktx.kodein.db.operation.putMissingById
import com.bselzer.ktx.kodein.db.transaction.transaction
import com.bselzer.ktx.logging.Logger
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Singleton
@Inject
class ContinentRepository(
    dependencies: RepositoryDependencies,
    private val repositories: Repositories
) : RepositoryDependencies by dependencies {
    @Singleton
    @Inject
    data class Repositories(
        val translation: TranslationRepository
    )

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

        repositories.translation.updateTranslations(
            translator = Gw2Translators.continent,
            defaults = listOf(continent),
            requestTranslated = { missing, language -> clients.gw2.continent.continents(missing, language) }
        )
    }

    private suspend fun updateFloor(continentId: ContinentId, floorId: FloorId) = database.transaction().use {
        Logger.d { "Continent | Updating floor $floorId in continent $continentId." }

        val floor = getById(
            id = floorId,
            requestSingle = { clients.gw2.continent.floor(continentId, floorId) }
        )

        _floors[floor.id] = floor

        repositories.translation.updateTranslations(
            translator = Gw2Translators.floor,
            defaults = listOf(floor),
            requestTranslated = { missing, language -> clients.gw2.continent.floors(continentId, missing, language) }
        )

        val mapIds = floor.regions.values.flatMap { region -> region.maps.keys }
        updateMaps(mapIds, floorId)
    }

    private suspend fun updateMaps(mapIds: Collection<MapId>, floorId: FloorId) = database.transaction().use {
        Logger.d { "Continent | Updating ${mapIds.size} maps in floor $floorId." }

        val maps = putMissingById(
            requestIds = { mapIds },
            requestById = { missingIds -> clients.gw2.map.maps(missingIds) }
        )

        maps.putInto(_maps)

        repositories.translation.updateTranslations(
            translator = Gw2Translators.map,
            defaults = maps.values,
            requestTranslated = { missing, language -> clients.gw2.map.maps(missing, language) }
        )
    }
}