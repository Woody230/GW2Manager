package com.bselzer.gw2.manager.common.repository

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.model.wvw.WvwContinent
import com.bselzer.gw2.v2.model.continent.Continent
import com.bselzer.gw2.v2.model.continent.ContinentId
import com.bselzer.gw2.v2.model.continent.floor.Floor
import com.bselzer.gw2.v2.model.continent.floor.FloorId
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgradeId
import com.bselzer.gw2.v2.model.map.MapId
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.gw2.v2.tile.model.request.TileGridRequest
import com.bselzer.gw2.v2.tile.model.response.TileGrid
import com.bselzer.ktx.logging.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class WvwRepository(dependencies: RepositoryDependencies) : AppRepository(dependencies) {
    /**
     * The match associated with the selected world.
     */
    fun selectedMatch(): Flow<WvwMatch?> = preferences.wvw.selectedWorld.observe().map { worldId ->
        transaction {
            with(caches.gw2.wvw) {
                if (worldId.isDefault) {
                    null
                } else {
                    // MUST call putMatch for objectives and upgrades to be populated.
                    findMatch(worldId).also { putMatch(it) }
                }
            }
        }
    }

    /**
     * The objectives associated with the match for the selected world.
     */
    fun selectedMatchObjectives(): Flow<Collection<WvwObjective>> = selectedMatch().filterNotNull().map { match ->
        transaction {
            with(caches.gw2.wvw) {
                findObjectives(match)
            }
        }
    }

    /**
     * The upgrades associated with the objectives for the selected world's match.
     */
    fun selectedMatchUpgrades(): Flow<Collection<WvwUpgrade>> = selectedMatchObjectives().map { objectives ->
        transaction {
            with(caches.gw2.wvw) {
                findUpgrades(objectives)
            }
        }
    }

    /**
     * The guild upgrades associated with the objectives for the selected world's match.
     */
    fun selectedMatchGuildUpgrades(): Flow<Collection<GuildUpgrade>> = selectedMatch().map { match ->
        transaction {
            with(caches.gw2.wvw) {
                val objectives = match?.maps?.flatMap { map -> map.objectives } ?: emptyList()
                if (objectives.isEmpty()) emptyList() else findGuildUpgrades(objectives)
            }
        }
    }

    /**
     * The guild upgrades associated declared in the configuration.
     */
    fun configuredGuildUpgrades(): Flow<Collection<GuildUpgrade>> = flow {
        transaction {
            with(caches.gw2.wvw) {
                // Set up all the configured guild upgrades since there is no direct way to know what upgrades are associated with each tier.
                val improvementIds = configuration.wvw.objectives.guildUpgrades.improvements.flatMap { improvement -> improvement.upgrades.map { upgrade -> upgrade.id } }
                val tacticIds = configuration.wvw.objectives.guildUpgrades.tactics.flatMap { tactic -> tactic.upgrades.map { upgrade -> upgrade.id } }
                val allIds = (improvementIds + tacticIds).map { id -> GuildUpgradeId(id) }
                val guildUpgrades = findGuildUpgrades(ids = allIds)
                emit(guildUpgrades)
            }
        }
    }

    /**
     * The continent associated with a map in the selected world's match, or the configured continent if the id can't be retrieved.
     */
    fun selectedMatchContinent(): Flow<WvwContinent> = selectedMatch().map { match ->
        // Assume that all WvW maps are within the same continent and floor.
        continent(mapId = match?.maps?.firstOrNull()?.id)
    }

    /**
     * The continent associated with the map that has the given [mapId], or the configured continent if the [mapId] is null.
     */
    private suspend fun continent(mapId: MapId?): WvwContinent = transaction {
        with(caches.gw2.continent) {
            if (mapId == null) {
                // Default to what is in the config to determine the correct continent.
                val continentId = ContinentId(configuration.wvw.map.continentId)
                val floorId = FloorId(configuration.wvw.map.floorId)
                WvwContinent(
                    continent = getContinent(continentId),
                    floor = getContinentFloor(continentId, floorId),
                )
            } else {
                // Get the associated continent from the map.
                val map = getMap(mapId)
                WvwContinent(
                    continent = getContinent(map),
                    floor = getContinentFloor(map)
                )
            }
        }
    }

    /**
     * The grid with tile content populated for the selected world's match's continent.
     */
    fun selectedMatchGrid(zoom: Int) = selectedMatchContinent().map { continent ->
        grid(continent.continent, continent.floor, zoom)
    }

    /**
     * Creates a grid with tile content populated. The grid may be bounded to configured size.
     */
    private suspend fun grid(continent: Continent, floor: Floor, zoom: Int) = gridRequest(continent, floor, zoom).let { request ->
        TileGrid(
            request = request,
            tiles = transaction {
                with(caches.tile) {
                    // TODO get tiles on as needed basis
                    request.tileRequests.map { request -> getTile(request) }
                }
            }
        )
    }

    /**
     * Creates the request for the grid, which may be bounded to a configured size.
     */
    private suspend fun gridRequest(continent: Continent, floor: Floor, zoom: Int): TileGridRequest = clients.tile.requestGrid(
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