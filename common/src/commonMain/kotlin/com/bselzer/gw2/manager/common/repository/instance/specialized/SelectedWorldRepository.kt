package com.bselzer.gw2.manager.common.repository.instance.specialized

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.v2.model.continent.Continent
import com.bselzer.gw2.v2.model.continent.floor.Floor
import com.bselzer.gw2.v2.model.map.MapId
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.world.WorldId
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.settings.compose.nullState
import com.bselzer.ktx.settings.compose.safeState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class SelectedWorldRepository(
    dependencies: RepositoryDependencies,
    private val repositories: SelectedWorldRepositories
) : RepositoryDependencies by dependencies, SelectedWorldData, MapData by repositories.map, MatchData by repositories.match {
    private val mapId: MapId?
        get() = repositories.match.match?.mapId()

    /**
     * The continent for the current match.
     */
    override val continent: Continent?
        get() = repositories.map.getContinent(mapId)

    /**
     * The floor for the current match.
     */
    override val floor: Floor?
        get() = repositories.map.getFloor(mapId)

    private val _worldId = mutableStateOf<WorldId?>(null)
    override val worldId: WorldId?
        get() = _worldId.value
    override val world: World?
        get() = repositories.world.worlds[_worldId.value]

    /**
     * Updates the grid to the new [zoom] level with the current match's map.
     */
    override suspend fun updateZoom(zoom: Int) = repositories.map.updateZoom(zoom, mapId)

    /**
     * Refreshes the associated [WvwMatchRepository] and [MapRepository] based on the interval defined in the preferences.
     */
    @Composable
    fun Refresh() {
        val refreshInterval by preferences.wvw.refreshInterval.safeState()
        val lastRefresh by preferences.wvw.lastRefresh.safeState()
        LaunchedEffect(refreshInterval, lastRefresh) {
            val remaining = refreshInterval - Clock.System.now().minus(lastRefresh)

            // Since the initial value is the distant future, this will initially wait forever until its true value is retrieved.
            // Therefore, an initializer is used to ensure that the last refresh exists so ensure this does not actually happen.
            if (remaining.isPositive()) {
                Logger.d { "Selected World | Refresh Interval | Last refreshed at $lastRefresh. Waiting for $remaining." }
                delay(remaining)
            }

            forceRefresh()
        }
    }

    /**
     * Updates the world based on the id defined in the preferences.
     */
    @Composable
    fun UpdateWorld() {
        val id = preferences.wvw.selectedWorld.nullState().value
        LaunchedEffect(id) {
            Logger.d { "Selected World | Updating world id to $id." }
            _worldId.value = id

            // Since the initial value will be null, only force refresh when the id exists.
            if (id != null) {
                forceRefresh()
            }
        }
    }

    /**
     * Refreshes the associated [WvwMatchRepository] and [MapRepository] regardless of when the last refresh occurred.
     */
    override suspend fun forceRefresh() {
        repositories.world.updateWorlds()
        worldId?.let { updateMatch(it) }

        val now = Clock.System.now()
        Logger.d { "Selected World | Refresh Interval | Refresh complete at $now." }
        preferences.wvw.lastRefresh.set(now)
    }

    private suspend fun updateMatch(worldId: WorldId) = coroutineScope {
        Logger.d { "Selected World | Update Match | Refreshing with world id $worldId." }

        // TODO refresh grid only when map lifecycle is active
        val match = clients.gw2.wvw.match(worldId)
        launch { repositories.map.updateContinent(match.mapId()) }
        launch { repositories.match.updateMatch(match) }
    }

    /**
     * Gets the first map's id.
     *
     * It is assumed that all World vs. World maps are within the same continent and floor.
     */
    private fun WvwMatch.mapId() = maps.firstOrNull()?.id
}