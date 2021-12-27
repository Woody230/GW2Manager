package com.bselzer.gw2.manager.common.state.map

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.common.state.map.grid.GridState
import com.bselzer.gw2.manager.common.state.map.grid.TileCount
import com.bselzer.gw2.manager.common.state.map.grid.TileState
import com.bselzer.gw2.manager.common.state.map.objective.*
import com.bselzer.gw2.manager.common.state.WvwHelper.color
import com.bselzer.gw2.manager.common.state.WvwHelper.objective
import com.bselzer.gw2.manager.common.state.WvwHelper.selectedDateFormatted
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.gw2.manager.common.state.core.Gw2State
import com.bselzer.gw2.manager.common.ui.composable.ImageState
import com.bselzer.gw2.v2.cache.instance.ContinentCache
import com.bselzer.gw2.v2.model.continent.Continent
import com.bselzer.gw2.v2.model.continent.ContinentFloor
import com.bselzer.gw2.v2.model.enumeration.extension.wvw.owner
import com.bselzer.gw2.v2.model.enumeration.extension.wvw.type
import com.bselzer.gw2.v2.model.enumeration.wvw.MapBonusType
import com.bselzer.gw2.v2.model.enumeration.wvw.MapType
import com.bselzer.gw2.v2.model.enumeration.wvw.ObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.wvw.ObjectiveType
import com.bselzer.gw2.v2.model.extension.continent.continentRectangle
import com.bselzer.gw2.v2.model.extension.wvw.*
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.gw2.v2.tile.model.response.Tile
import com.bselzer.gw2.v2.tile.model.response.TileGrid
import com.bselzer.ktx.compose.ui.style.Hex
import com.bselzer.ktx.compose.ui.style.color
import com.bselzer.ktx.function.collection.isOneOf
import com.bselzer.ktx.function.objects.userFriendly
import com.bselzer.ktx.geometry.dimension.bi.position.Point2D
import com.bselzer.ktx.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.ExperimentalTime

class WvwMapState(
    private val state: Gw2State
) : Gw2State by state {
    // Scroll control is exposed through shouldScrollToRegion and should not be exposed here as well.
    private val enableScrollToRegion = mutableStateOf(true)
    private val zoom = mutableStateOf(state.configuration.wvw.map.zoom.default)
    val selectedObjective = mutableStateOf<WvwObjective?>(null)
    val grid = mutableStateOf(TileGrid())
    val tileContent = mutableStateMapOf<Tile, ByteArray>()
    val continent = mutableStateOf<Continent?>(null)
    val floor = mutableStateOf<ContinentFloor?>(null)
    val horizontalScroll: ScrollState = ScrollState(0)
    val verticalScroll: ScrollState = ScrollState(0)

    /**
     * Updates the zoom to be within the configured range.
     */
    fun changeZoom(increment: Int) {
        val currentZoom = currentZoom()

        // Must keep the zoom bounded within the configured range.
        val min = configuration.wvw.map.zoom.min
        val max = configuration.wvw.map.zoom.max
        zoom.value = Integer.max(min, Integer.min(max, currentZoom + increment))
    }

    /**
     * The current zoom level.
     */
    fun currentZoom() = zoom.value

    /**
     * The zoom level restrictive range.
     */
    fun zoomRange(): IntRange = IntRange(start = configuration.wvw.map.zoom.min, endInclusive = configuration.wvw.map.zoom.max)

    /**
     * The number of tiles with content mapped to the total number of tiles for the current zoom level.
     */
    private val tileCount: State<TileCount> = derivedStateOf {
        val zoom by zoom
        val contentSize = tileContent.filterKeys { key -> key.zoom == zoom }.size
        TileCount(contentSize = contentSize, gridSize = grid.value.tiles.size)
    }

    /**
     * Whether to display a progress bar until tiling is finished.
     */
    val shouldShowMissingGridData: State<Boolean> = derivedStateOf {
        val tileCount = tileCount.value
        tileCount.isEmpty || !tileCount.hasAllContent
    }

    /**
     * Whether to show the objective icons.
     */
    val shouldShowObjectives: State<Boolean> = derivedStateOf { !shouldShowMissingGridData.value }

    /**
     * Whether to scroll the map to the configured region.
     */
    var shouldScrollToRegion: MutableState<Boolean> = object : MutableState<Boolean> {
        val state = derivedStateOf { configuration.wvw.map.scroll.enabled && !tileCount.value.isEmpty && enableScrollToRegion.value }
        override var value: Boolean
            get() = this.state.value
            set(value) {
                enableScrollToRegion.value = value
            }

        override fun component1(): Boolean = value
        override fun component2(): (Boolean) -> Unit = { value = it }
    }

    /**
     * The coordinates to scroll to for the configured map.
     */
    val scrollToRegionCoordinates: State<Pair<Int, Int>> = derivedStateOf {
        val region = floor.value?.regions?.values?.firstOrNull { region -> region.name == configuration.wvw.map.regionName }
        val map = region?.maps?.values?.firstOrNull { map -> map.name == configuration.wvw.map.scroll.mapName } ?: return@derivedStateOf Pair(0, 0)
        val topLeft = map.continentRectangle().point1
        grid.value.scale(topLeft.x.toInt(), topLeft.y.toInt())
    }

    /**
     * Whether to show the bloodlust icons.
     */
    val shouldShowBloodlust: State<Boolean> = derivedStateOf { configuration.wvw.bloodlust.enabled && !shouldShowMissingGridData.value }

    /**
     * The state of the bloodlust icons.
     */
    val bloodlusts: State<Collection<BloodlustState>> = derivedStateOf {
        val match = worldMatch.value ?: return@derivedStateOf emptyList<BloodlustState>()

        val width = configuration.wvw.bloodlust.size.width
        val height = configuration.wvw.bloodlust.size.height

        val borderlands = match.maps.filter { map -> map.type().isOneOf(MapType.BLUE_BORDERLANDS, MapType.RED_BORDERLANDS, MapType.GREEN_BORDERLANDS) }
        borderlands.mapNotNull { borderland ->
            val matchRuins = borderland.objectives.filter { objective -> objective.type() == ObjectiveType.RUINS }
            if (matchRuins.isEmpty()) {
                Logger.w("There are no ruins on map ${borderland.id}.")
                return@mapNotNull null
            }

            val objectiveRuins = matchRuins.mapNotNull { ruin -> worldObjectives.value.firstOrNull { objective -> objective.id == ruin.id } }
            if (objectiveRuins.count() != matchRuins.count()) {
                Logger.w("Mismatch between the number of ruins in the match and objectives on map ${borderland.id}.")
                return@mapNotNull null
            }

            // Use the center of all of the ruins as the position of the bloodlust icon.
            val x = objectiveRuins.sumOf { ruin -> ruin.coordinates().x } / objectiveRuins.count()
            val y = objectiveRuins.sumOf { ruin -> ruin.coordinates().y } / objectiveRuins.count()

            // Scale the position before using it.
            val coordinates = Point2D(x, y).scaledCoordinates(width, height)

            val owner = borderland.bonuses.firstOrNull { bonus -> bonus.type() == MapBonusType.BLOODLUST }?.owner() ?: ObjectiveOwner.NEUTRAL
            BloodlustState(
                link = configuration.wvw.bloodlust.iconLink,
                color = configuration.wvw.color(owner = owner),
                x = coordinates.x.toInt(),
                y = coordinates.y.toInt(),
                width = width,
                height = height,
                description = "${owner.userFriendly()} Bloodlust",
                enabled = shouldShowBloodlust.value
            )
        }
    }

    /**
     * The state of the grid on the map.
     */
    val mapGrid: State<GridState> = derivedStateOf {
        val grid by grid
        GridState(
            objectives = mapObjectives.value,
            tiles = grid.rows.map { row ->
                row.map { tile ->
                    TileState(
                        width = grid.tileWidth,
                        height = grid.tileHeight,
                        content = tileContent[tile] ?: ByteArray(0)
                    )
                }
            },
        )
    }

    /**
     * The state of the objectives on the map for the current match.
     */
    @OptIn(ExperimentalTime::class)
    private val mapObjectives: State<Collection<ObjectiveState>> = derivedStateOf {
        val match = worldMatch.value ?: return@derivedStateOf emptyList<ObjectiveState>()
        worldObjectives.value.mapNotNull { objective ->
            val fromConfig = configuration.wvw.objective(objective)
            val fromMatch = match.objective(objective) ?: return@mapNotNull null

            val upgrade = upgrades[objective.upgradeId]
            val tiers = upgrade?.tiers(fromMatch.yaksDelivered)?.flatMap { tier -> tier.upgrades } ?: emptyList()

            val size = fromConfig?.size ?: configuration.wvw.objectives.defaultSize
            val coordinates = objective.position().scaledCoordinates(size.width, size.height)

            // Get the progression level associated with the current number of yaks delivered to the objective.
            val progression = upgrade?.let {
                val level = upgrade.level(fromMatch.yaksDelivered)
                configuration.wvw.objectives.progressions.progression.getOrNull(level)
            }
            val progressionSize = progression?.size ?: configuration.wvw.objectives.progressions.defaultSize

            // See if any of the progressed tiers has a permanent waypoint upgrade, or the tactic for the temporary waypoint.
            val hasWaypointUpgrade = tiers.any { tier -> configuration.wvw.objectives.waypoint.upgradeNameRegex.matches(tier.name) }
            val hasWaypointTactic = configuration.wvw.objectives.waypoint.guild.enabled && fromMatch.guildUpgradeIds.mapNotNull { id -> guildUpgrades[id] }
                .any { tactic -> configuration.wvw.objectives.waypoint.guild.upgradeNameRegex.matches(tactic.name) }

            ObjectiveState(
                objective = objective,
                x = coordinates.x.toInt(),
                y = coordinates.y.toInt(),
                width = size.width,
                height = size.height,
                progression = ProgressionState(
                    enabled = configuration.wvw.objectives.progressions.enabled && progression != null,
                    link = progression?.iconLink,
                    width = progressionSize.width,
                    height = progressionSize.height
                ),
                claim = ClaimState(
                    enabled = configuration.wvw.objectives.claim.enabled && !fromMatch.claimedBy.isNullOrBlank(),
                    link = configuration.wvw.objectives.claim.iconLink,
                    width = configuration.wvw.objectives.claim.size.width,
                    height = configuration.wvw.objectives.claim.size.height
                ),
                waypoint = WaypointState(
                    enabled = configuration.wvw.objectives.waypoint.enabled && (hasWaypointUpgrade || hasWaypointTactic),
                    link = configuration.wvw.objectives.waypoint.iconLink,
                    width = configuration.wvw.objectives.waypoint.size.width,
                    height = configuration.wvw.objectives.waypoint.size.height,
                    color = if (hasWaypointTactic && !hasWaypointUpgrade) Hex(configuration.wvw.objectives.waypoint.guild.color).color() else null
                ),
                immunity = ImmunityState(
                    enabled = configuration.wvw.objectives.immunity.enabled,
                    textSize = configuration.wvw.objectives.immunity.textSize.sp,
                    duration = fromConfig?.immunity ?: configuration.wvw.objectives.immunity.defaultDuration,
                    startTime = fromMatch.lastFlippedAt,
                    delay = configuration.wvw.objectives.immunity.delay
                ),
                image = object : ImageState {
                    // Use a default link when the icon link doesn't exist. The link won't exist for atypical types such as Spawn/Mercenary.
                    override val link = if (objective.iconLink.isBlank()) fromConfig?.defaultIconLink else objective.iconLink
                    override val description = objective.name
                    override val color = configuration.wvw.color(fromMatch)
                    override val width = size.width
                    override val height = size.height
                    override val enabled = true
                }
            )
        }
    }

    /**
     * The state of the objective selected by the user on the map.
     */
    val mapSelectedObjective: State<SelectedObjectiveState?> = derivedStateOf {
        val objective = selectedObjective.value ?: return@derivedStateOf null
        val fromMatch = worldMatch.value.objective(objective)
        val owner = fromMatch?.owner() ?: ObjectiveOwner.NEUTRAL
        SelectedObjectiveState(
            title = "${objective.name} (${owner.userFriendly()} ${objective.type})",
            subtitle = fromMatch?.lastFlippedAt?.let { lastFlippedAt ->
                "Flipped at ${configuration.wvw.selectedDateFormatted(lastFlippedAt)}"
            },
            textSize = configuration.wvw.objectives.selected.textSize.sp
        )
    }

    /**
     * @return the scaled coordinates of the image
     */
    private fun Point2D.scaledCoordinates(width: Number, height: Number): Point2D =
        // Scale the objective coordinates to the zoom level and remove excluded bounds.
        grid.value.scale(x.toInt(), y.toInt()).run {
            // Displace the coordinates so that it aligns with the center of the image.
            copy(x = first - width.toDouble() / 2, y = second - height.toDouble() / 2)
        }

    // region Refresh

    /**
     * Refreshes the WvW map tiling grid.
     */
    suspend fun refreshGridData() = withContext(Dispatchers.IO) {
        gw2Cache.instance {
            val continent = continent.value
            val floor = floor.value

            // Verify that the related data exists.
            if (continent == null || floor == null) {
                return@instance
            }

            val zoom = currentZoom()
            Logger.d("Refreshing WvW tile grid data for zoom level $zoom.")

            val gridRequest = tileClient.requestGrid(continent, floor, zoom).let { request ->
                if (configuration.wvw.map.isBounded) {
                    // Cut off unneeded tiles.
                    val bound = configuration.wvw.map.levels.firstOrNull { level -> level.zoom == zoom }?.bound
                    if (bound != null) {
                        return@let request.bounded(startX = bound.startX, startY = bound.startY, endX = bound.endX, endY = bound.endY)
                    } else {
                        Logger.w("Unable to create a bounded request for zoom level $zoom.")
                    }
                }

                return@let request
            }

            // Set up the grid without content in the tiles.
            grid.value = TileGrid(gridRequest, gridRequest.tileRequests.map { tileRequest -> Tile(tileRequest) })

            // Defer the content for parallelism and populate it when its ready.
            for (deferred in tileCache.findTilesAsync(gridRequest.tileRequests)) {
                val tile = deferred.await()
                tileContent[tile] = tile.content
            }
        }
    }

    /**
     * Refreshes the WvW map data using the configuration ids.
     */
    suspend fun refreshMapData() = withContext(Dispatchers.IO) {
        gw2Cache.instance {
            Logger.d("Refreshing WvW map data.")

            // Assume that all WvW maps are within the same continent and floor.
            val mapId = worldMatch.value?.maps?.firstOrNull()?.id
            if (mapId == null) {
                // Default to what is in the config to determine the correct continent.
                val cache = get<ContinentCache>()
                val continent = cache.getContinent(configuration.wvw.map.continentId)
                floor.value = cache.getContinentFloor(configuration.wvw.map.continentId, configuration.wvw.map.floorId)
                this@WvwMapState.continent.value = continent
            } else {
                // Get the associated continent from the map.
                val cache = get<ContinentCache>()
                val map = cache.getMap(mapId)
                val continent = cache.getContinent(map)
                floor.value = cache.getContinentFloor(map)
                this@WvwMapState.continent.value = continent
            }
        }
    }

    // endregion Refresh
}