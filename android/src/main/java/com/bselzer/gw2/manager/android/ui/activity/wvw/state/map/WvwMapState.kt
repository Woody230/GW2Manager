package com.bselzer.gw2.manager.android.ui.activity.wvw.state.map

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.android.ui.activity.wvw.WvwHelper.color
import com.bselzer.gw2.manager.android.ui.activity.wvw.WvwHelper.objective
import com.bselzer.gw2.manager.android.ui.activity.wvw.WvwHelper.selectedDateFormatted
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.common.ImageState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.*
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.grid.GridState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.grid.TileCount
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.grid.TileState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.objective.*
import com.bselzer.gw2.manager.common.configuration.wvw.Wvw
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
import kotlin.time.ExperimentalTime

data class WvwMapState(
    private val configuration: Wvw,
    private val match: State<WvwMatch?>,
    private val objectives: State<Collection<WvwObjective>>,
    private val selectedObjective: MutableState<WvwObjective?>,
    private val upgrades: State<Map<Int, WvwUpgrade>>,
    private val guildUpgrades: State<Map<Int, GuildUpgrade>>,
    private val zoom: MutableState<Int>,
    private val continent: State<Continent?>,
    private val floor: State<ContinentFloor?>,
    private val grid: State<TileGrid>,
    private val tileContent: Map<Tile, ByteArray>,

    // Scroll control is exposed through shouldScrollToRegion and should not be exposed here as well.
    private val enableScrollToRegion: MutableState<Boolean>,
    val horizontalScroll: ScrollState,
    val verticalScroll: ScrollState,
) {
    /**
     * Updates the zoom to be within the configured range.
     */
    fun changeZoom(increment: Int) {
        val currentZoom = currentZoom()

        // Must keep the zoom bounded within the configured range.
        val min = configuration.map.zoom.min
        val max = configuration.map.zoom.max
        zoom.value = Integer.max(min, Integer.min(max, currentZoom + increment))
    }

    /**
     * The current zoom level.
     */
    fun currentZoom() = zoom.value

    /**
     * The zoom level restrictive range.
     */
    fun zoomRange(): IntRange = IntRange(start = configuration.map.zoom.min, endInclusive = configuration.map.zoom.max)

    /**
     * Selects the [objective], or clears it if it is null.
     */
    fun select(objective: WvwObjective?) {
        selectedObjective.value = objective
    }

    /**
     * The number of tiles with content mapped to the total number of tiles for the current zoom level.
     */
    val tileCount: State<TileCount> = derivedStateOf {
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
     * Whether to scroll the map to the configured region.
     */
    var shouldScrollToRegion: MutableState<Boolean> = object : MutableState<Boolean> {
        val state = derivedStateOf { configuration.map.scroll.enabled && !tileCount.value.isEmpty && enableScrollToRegion.value }
        override var value: Boolean
            get() = state.value
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
        val region = floor.value?.regions?.values?.firstOrNull { region -> region.name == configuration.map.regionName }
        val map = region?.maps?.values?.firstOrNull { map -> map.name == configuration.map.scroll.mapName } ?: return@derivedStateOf Pair(0, 0)
        val topLeft = map.continentRectangle().point1
        grid.value.scale(topLeft.x.toInt(), topLeft.y.toInt())
    }

    /**
     * Whether to show the bloodlust icons.
     */
    val shouldShowBloodlust: State<Boolean> = derivedStateOf { configuration.bloodlust.enabled && tileCount.value.hasAllContent && bloodlusts.value.isNotEmpty() }

    /**
     * The state of the bloodlust icons.
     */
    val bloodlusts: State<Collection<BloodlustState>> = derivedStateOf {
        val match = match.value ?: return@derivedStateOf emptyList<BloodlustState>()

        val width = configuration.bloodlust.size.width
        val height = configuration.bloodlust.size.height

        val borderlands = match.maps.filter { map -> map.type().isOneOf(MapType.BLUE_BORDERLANDS, MapType.RED_BORDERLANDS, MapType.GREEN_BORDERLANDS) }
        borderlands.mapNotNull { borderland ->
            val matchRuins = borderland.objectives.filter { objective -> objective.type() == ObjectiveType.RUINS }
            if (matchRuins.isEmpty()) {
                Logger.w("There are no ruins on map ${borderland.id}.")
                return@mapNotNull null
            }

            val objectiveRuins = matchRuins.mapNotNull { ruin -> objectives.value.firstOrNull { objective -> objective.id == ruin.id } }
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
                link = configuration.bloodlust.iconLink,
                color = configuration.color(owner = owner),
                x = coordinates.x.toInt(),
                y = coordinates.y.toInt(),
                width = width,
                height = height,
                description = "${owner.userFriendly()} Bloodlust"
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
        val match = match.value ?: return@derivedStateOf emptyList<ObjectiveState>()
        val upgrades = upgrades.value
        val guildUpgrades = guildUpgrades.value
        objectives.value.mapNotNull { objective ->
            val fromConfig = configuration.objective(objective)
            val fromMatch = match.objective(objective) ?: return@mapNotNull null

            val upgrade = upgrades[objective.upgradeId]
            val tiers = upgrade?.tiers(fromMatch.yaksDelivered)?.flatMap { tier -> tier.upgrades } ?: emptyList()

            val size = fromConfig?.size ?: configuration.objectives.defaultSize
            val coordinates = objective.position().scaledCoordinates(size.width, size.height)

            // Get the progression level associated with the current number of yaks delivered to the objective.
            val progression = upgrade?.let {
                val level = upgrade.level(fromMatch.yaksDelivered)
                configuration.objectives.progressions.progression.getOrNull(level)
            }
            val progressionSize = progression?.size ?: configuration.objectives.progressions.defaultSize

            // See if any of the progressed tiers has a permanent waypoint upgrade, or the tactic for the temporary waypoint.
            val hasWaypointUpgrade = tiers.any { tier -> configuration.objectives.waypoint.upgradeNameRegex.matches(tier.name) }
            val hasWaypointTactic = configuration.objectives.waypoint.guild.enabled && fromMatch.guildUpgradeIds.mapNotNull { id -> guildUpgrades[id] }
                .any { tactic -> configuration.objectives.waypoint.guild.upgradeNameRegex.matches(tactic.name) }

            ObjectiveState(
                objective = objective,
                x = coordinates.x.toInt(),
                y = coordinates.y.toInt(),
                width = size.width,
                height = size.height,
                progression = ProgressionState(
                    enabled = configuration.objectives.progressions.enabled && progression != null,
                    link = progression?.iconLink,
                    width = progressionSize.width,
                    height = progressionSize.height
                ),
                claim = ClaimState(
                    enabled = configuration.objectives.claim.enabled && !fromMatch.claimedBy.isNullOrBlank(),
                    link = configuration.objectives.claim.iconLink,
                    width = configuration.objectives.claim.size.width,
                    height = configuration.objectives.claim.size.height
                ),
                waypoint = WaypointState(
                    enabled = configuration.objectives.waypoint.enabled && (hasWaypointUpgrade || hasWaypointTactic),
                    link = configuration.objectives.waypoint.iconLink,
                    width = configuration.objectives.waypoint.size.width,
                    height = configuration.objectives.waypoint.size.height,
                    color = if (hasWaypointTactic && !hasWaypointUpgrade) Hex(configuration.objectives.waypoint.guild.color).color() else null
                ),
                immunity = ImmunityState(
                    enabled = configuration.objectives.immunity.enabled,
                    textSize = configuration.objectives.immunity.textSize.sp,
                    duration = fromConfig?.immunity ?: configuration.objectives.immunity.defaultDuration,
                    startTime = fromMatch.lastFlippedAt,
                    delay = configuration.objectives.immunity.delay
                ),
                image = ImageState(
                    // Use a default link when the icon link doesn't exist. The link won't exist for atypical types such as Spawn/Mercenary.
                    link = if (objective.iconLink.isBlank()) fromConfig?.defaultIconLink else objective.iconLink,
                    description = objective.name,
                    color = configuration.color(fromMatch),
                    width = size.width,
                    height = size.height,
                )
            )
        }
    }

    /**
     * The state of the objective selected by the user on the map.
     */
    val mapSelectedObjective: State<SelectedObjectiveState?> = derivedStateOf {
        val objective = selectedObjective.value ?: return@derivedStateOf null
        val fromMatch = match.value.objective(objective)
        val owner = fromMatch?.owner() ?: ObjectiveOwner.NEUTRAL
        SelectedObjectiveState(
            title = "${objective.name} (${owner.userFriendly()} ${objective.type})",
            subtitle = fromMatch?.lastFlippedAt?.let { lastFlippedAt ->
                "Flipped at ${configuration.selectedDateFormatted(lastFlippedAt)}"
            },
            textSize = configuration.objectives.selected.textSize.sp
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
}