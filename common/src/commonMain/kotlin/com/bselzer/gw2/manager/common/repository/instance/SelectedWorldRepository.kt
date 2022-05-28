package com.bselzer.gw2.manager.common.repository.instance

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.base.GenericRepositories
import com.bselzer.gw2.manager.common.repository.base.SpecializedRepository
import com.bselzer.gw2.manager.common.repository.model.continent.ContinentFloor
import com.bselzer.gw2.v2.model.extension.wvw.allWorlds
import com.bselzer.gw2.v2.model.extension.wvw.guildUpgradeIds
import com.bselzer.gw2.v2.model.extension.wvw.objectiveIds
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgradeId
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.world.WorldId
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.model.wvw.objective.WvwMapObjectiveId
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgradeId
import com.bselzer.gw2.v2.tile.cache.metadata.id
import com.bselzer.gw2.v2.tile.model.response.Tile
import com.bselzer.gw2.v2.tile.model.response.TileGrid
import com.bselzer.ktx.kodein.db.operation.findByIds
import com.bselzer.ktx.kodein.db.operation.putMissingById
import com.bselzer.ktx.kodein.db.transaction.Transaction
import com.bselzer.ktx.kodein.db.transaction.transaction
import com.bselzer.ktx.logging.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.kodein.db.find
import org.kodein.db.getById
import org.kodein.db.useModels
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration

class SelectedWorldRepository(
    dependencies: RepositoryDependencies,
    repositories: GenericRepositories
) : SpecializedRepository(dependencies, repositories) {
    val world: StateFlow<World?> = preferences.wvw.selectedWorld.observe().combine(repositories.world.worlds) { worldId, worlds ->
        worlds.firstOrNull { world -> world.id == worldId }
    }.stateIn(
        // The world is required to find the associated match so resolve it as soon as possible.
        started = SharingStarted.Eagerly,
        initialValue = null,
        scope = scope,
    )

    val match: StateFlow<WvwMatch?> = world.map { world ->
        if (world == null) {
            return@map null
        }

        database.find<WvwMatch>().all().useModels { matches ->
            matches.firstOrNull { match -> match.allWorlds().contains(world.id) }
        }
    }.stateIn(
        // The match is required for finding all remaining data so resolve it as soon as possible.
        started = SharingStarted.Eagerly,
        initialValue = null,
        scope = scope,
    )

    val objectives: StateFlow<Map<WvwMapObjectiveId, WvwObjective>> = match.map { match ->
        val ids = match?.objectiveIds() ?: emptyList()
        val models: Collection<WvwObjective> = database.findByIds(ids)
        models.associateBy { it.id }
    }.stateIn(
        // Objectives only matter for the map page so wait for subscription.
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyMap(),
        scope = scope,
    )

    val upgrades: StateFlow<Map<WvwUpgradeId, WvwUpgrade>> = objectives.map { objectives ->
        val ids = objectives.values.map { objective -> objective.upgradeId }
        val models: Collection<WvwUpgrade> = database.findByIds(ids)
        models.associateBy { it.id }
    }.stateIn(
        // Upgrades are dependent on objectives so wait for subscription.
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyMap(),
        scope = scope,
    )

    val guildUpgrades: StateFlow<Map<GuildUpgradeId, GuildUpgrade>> = match.map { match ->
        val ids = match?.guildUpgradeIds() ?: emptyList()
        val models: Collection<GuildUpgrade> = database.findByIds(ids)
        models.associateBy { it.id }
    }.stateIn(
        // Guild upgrades only matter for the map page so wait for subscription.
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyMap(),
        scope = scope,
    )

    val continent: StateFlow<ContinentFloor?> = match.map { match ->
        // Assume that all WvW maps are within the same continent and floor.
        val mapId = match?.maps?.firstOrNull()?.id
        if (mapId == null) {
            // Default to what is in the config to determine the correct continent.
            repositories.continent.getWvwContinent()
        } else {
            // Get the associated continent from the map.
            repositories.continent.getContinent(mapId)
        }
    }.stateIn(
        // The continent only matters for the map page so wait for subscription.
        started = SharingStarted.WhileSubscribed(),
        initialValue = null,
        scope = scope,
    )

    val zoomRange: IntRange = IntRange(start = configuration.wvw.map.zoom.min, endInclusive = configuration.wvw.map.zoom.max)
    private val _zoom = MutableStateFlow(configuration.wvw.map.zoom.default)
    val zoom: StateFlow<Int> = _zoom

    val grid: StateFlow<TileGrid?> = continent.combine(zoom) { wrapper, zoom ->
        if (wrapper == null) {
            null
        } else {
            val request = repositories.tile.getGridRequest(wrapper.continent, wrapper.floor, zoom)
            val tiles = request.tileRequests.map { tileRequest -> database.getById(tileRequest.id()) ?: Tile(tileRequest) }
            TileGrid(request, tiles)
        }
    }.stateIn(
        // The grid only matters for the map page so wait for subscription.
        // Expiration should be as soon as possible since tiles are memory intensive compared to everything else.
        started = SharingStarted.WhileSubscribed(stopTimeout = Duration.ZERO, replayExpiration = Duration.ZERO),
        initialValue = null,
        scope = scope,
    )

    private suspend fun updateMatch(worldId: WorldId) = coroutineScope {
        Logger.d { "Selected World | Refreshing match with world id $worldId." }

        val match = clients.gw2.wvw.match(worldId)
        launch { updateContinent(match) }
        launch { updateMap(match) }
    }

    /**
     * Updates the [grid] with the new [zoom] level.
     */
    suspend fun updateZoom(zoom: Int) {
        // Must keep the zoom bounded within the configured range.
        val bounded = max(zoomRange.first, min(zoomRange.last, zoom))
        _zoom.emit(bounded)

        val wrapper = continent.value
        if (wrapper != null) {
            repositories.tile.getGrid(wrapper.continent, wrapper.floor, bounded)
        }
    }

    /**
     * Updates the [continent] with the map associated with the [match].
     *
     * Updates the [grid] with the tiles for the current zoom level.
     */
    private suspend fun updateContinent(match: WvwMatch?) {
        // Assume that all WvW maps are within the same continent and floor.
        val mapId = match?.maps?.firstOrNull()?.id
        val continentFloor = if (mapId == null) {
            // Default to what is in the config to determine the correct continent.
            repositories.continent.getWvwContinent()
        } else {
            // Get the associated continent from the map.
            repositories.continent.getContinent(mapId)
        }

        // Only update the grid if its being subscribed to.
        // Replay expiration is set to zero, so if it is null then there must not be a subscription.
        if (grid.value != null) {
            repositories.tile.getGrid(continentFloor.continent, continentFloor.floor, zoom.value)
        }
    }

    /**
     * Updates the [match]'s [WvwObjective]s for each map and their associated [WvwUpgrade]s and claimable [GuildUpgrade]s.
     */
    private suspend fun updateMap(match: WvwMatch?) = database.transaction().use {
        updateMapObjectives(match)
        updateMapGuildUpgrades(match)
    }

    private suspend fun Transaction.updateMapObjectives(match: WvwMatch?) {
        val objectiveIds = match?.objectiveIds() ?: emptyList()
        putMissingById(
            requestIds = { objectiveIds },
            requestById = { missingIds -> clients.gw2.wvw.objectives(missingIds) }
        )

        val objectives: Collection<WvwObjective> = findByIds(objectiveIds)
        updateUpgrades(objectives)
    }

    private suspend fun Transaction.updateUpgrades(objectives: Collection<WvwObjective>) {
        val upgradeIds = objectives.map { objective -> objective.upgradeId }
        putMissingById(
            requestIds = { upgradeIds },
            requestById = { missingIds -> clients.gw2.wvw.upgrades(missingIds) },
            getId = { upgrade -> upgrade.id },

            // Need to default since some ids may not exist and this will prevent repeated API calls.
            default = { upgradeId -> WvwUpgrade(upgradeId) }
        )
    }

    private suspend fun Transaction.updateMapGuildUpgrades(match: WvwMatch?) {
        val guildUpgradeIds = match?.guildUpgradeIds() ?: emptyList()
        repositories.guild.getGuildUpgrades(guildUpgradeIds)
        repositories.guild.getConfiguredGuildUpgrades()
    }

    /**
     * The time remaining until a refresh is needed for World vs. World data.
     */
    private val timeUntilRefresh: StateFlow<Duration> = preferences.wvw.refreshInterval.observe().combine(
        preferences.wvw.lastRefresh.observe()
    ) { refreshInterval, lastRefresh ->
        if (lastRefresh == Instant.DISTANT_PAST) {
            Logger.d { "Refresh Interval | World vs. World | Refresh was not performed." }
            Duration.ZERO
        } else {
            refreshInterval - Clock.System.now().minus(lastRefresh).also { remaining ->
                Logger.d { "Refresh Interval | World vs. World | Last refreshed at $lastRefresh. Waiting for $remaining." }
            }
        }
    }.stateIn(
        started = SharingStarted.Eagerly,
        initialValue = preferences.wvw.refreshInterval.defaultValue,
        scope = scope,
    )

    /**
     * The current job for refreshing the data in this repository.
     */
    private val refreshJob = MutableStateFlow<Job?>(null)

    /**
     * Forces the refresh of World vs. World data.
     */
    suspend fun forceRefresh() = refreshMatch(Duration.ZERO)

    /**
     * Refreshes the match when the [timeUntilRefresh] reaches zero.
     */
    private suspend fun refreshMatch(timeUntilRefresh: Duration) {
        if (timeUntilRefresh.isPositive()) {
            Logger.d { "Refresh Interval | Job waiting for $timeUntilRefresh." }
            delay(timeUntilRefresh)
        }

        world.value?.id?.let { worldId -> updateMatch(worldId) }

        val now = Clock.System.now()
        Logger.d { "Last Refresh | Job complete at $now." }
        preferences.wvw.lastRefresh.set(now)
    }

    init {
        timeUntilRefresh.onEach { duration ->
            // Ensure that only one refresh is active at a time.
            refreshJob.getAndUpdate {
                scope.launch { refreshMatch(duration) }
            }?.cancel("Time until refresh updated to $duration.")
        }
    }
}