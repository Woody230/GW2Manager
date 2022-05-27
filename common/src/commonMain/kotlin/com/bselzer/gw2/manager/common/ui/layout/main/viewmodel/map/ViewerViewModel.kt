package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.*
import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.configuration.WvwHelper.color
import com.bselzer.gw2.manager.common.configuration.WvwHelper.objective
import com.bselzer.gw2.manager.common.configuration.WvwHelper.selectedDateFormatted
import com.bselzer.gw2.manager.common.configuration.WvwHelper.stringResource
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.Action
import com.bselzer.gw2.manager.common.ui.layout.main.model.map.*
import com.bselzer.gw2.v2.model.enumeration.WvwMapBonusType
import com.bselzer.gw2.v2.model.enumeration.WvwMapType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveType
import com.bselzer.gw2.v2.model.enumeration.extension.enumValueOrNull
import com.bselzer.gw2.v2.model.extension.wvw.level
import com.bselzer.gw2.v2.model.extension.wvw.objective
import com.bselzer.gw2.v2.model.extension.wvw.position
import com.bselzer.gw2.v2.model.extension.wvw.tiers
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.tile.model.response.TileGrid
import com.bselzer.ktx.compose.resource.ui.layout.icon.zoomInMapIconInteractor
import com.bselzer.ktx.compose.resource.ui.layout.icon.zoomOutMapIconInteractor
import com.bselzer.ktx.compose.ui.graphics.color.Hex
import com.bselzer.ktx.compose.ui.graphics.color.color
import com.bselzer.ktx.function.objects.isOneOf
import com.bselzer.ktx.geometry.dimension.bi.position.Point2D
import com.bselzer.ktx.logging.Logger
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.asImageUrl
import dev.icerock.moko.resources.desc.plus
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class ViewerViewModel(context: AppComponentContext, showDialog: (DialogConfig) -> Unit) : MapViewModel(context, showDialog) {
    override val title: StringDesc = Gw2Resources.strings.wvw_map.desc()

    private val zoomInAction
        get() = Action(
            enabled = zoom.value < zoomRange.last,
            icon = { zoomInMapIconInteractor() },
            onClick = { changeZoom(increment = 1) }
        )

    private val zoomOutAction
        get() = Action(
            enabled = zoom.value > zoomRange.first,
            icon = { zoomOutMapIconInteractor() },
            onClick = { changeZoom(increment = -1) }
        )

    override val actions: List<Action>
        get() = super.actions + listOf(zoomInAction, zoomOutAction)

    private val zoom: MutableState<Int> = mutableStateOf(configuration.wvw.map.zoom.default)
    private val zoomRange: IntRange = IntRange(start = configuration.wvw.map.zoom.min, endInclusive = configuration.wvw.map.zoom.max)

    /**
     * Updates the zoom to be within the configured range.
     */
    fun changeZoom(increment: Int) {
        val currentZoom = zoom.value

        // Must keep the zoom bounded within the configured range.
        zoom.value = max(zoomRange.first, min(zoomRange.last, currentZoom + increment))
    }

    /**
     * The grid of tiles without tile content.
     */
    val grid: State<TileGrid>
        @Composable
        get() = repositories.wvw.selectedMatchGrid(zoom.value).collectAsState(initial = TileGrid())

    val horizontalScroll: ScrollState = ScrollState(0)
    val verticalScroll: ScrollState = ScrollState(0)

    /**
     * Whether to scroll the map to the configured region.
     */
    val shouldScrollToRegion = mutableStateOf(configuration.wvw.map.scroll.enabled)

    /**
     * The coordinates to scroll to for the configured map.
     */
    private val scrollToRegionCoordinates: Pair<Int, Int>
        @Composable
        get() {
            val continent = repositories.wvw.selectedMatchContinent().collectAsState(null).value
            val region = continent?.floor?.regions?.values?.firstOrNull { region -> region.name == configuration.wvw.map.regionName }
            val map = region?.maps?.values?.firstOrNull { map -> map.name == configuration.wvw.map.scroll.mapName } ?: return Pair(0, 0)
            val topLeft = map.continentRectangle.point1
            return grid.value.scale(topLeft.x.toInt(), topLeft.y.toInt())
        }

    /**
     * Scrolls the map to the configured WvW map within the grid.
     */
    @Composable
    fun scrollToRegion() {
        if (shouldScrollToRegion.value) {
            val coordinates = scrollToRegionCoordinates
            rememberCoroutineScope().launch {
                horizontalScroll.animateScrollTo(coordinates.first)
                verticalScroll.animateScrollTo(coordinates.second)
                shouldScrollToRegion.value = false
            }
        }
    }

    val bloodlusts: Collection<Bloodlust>
        @Composable
        get() {
            val match = repositories.wvw.selectedMatch().collectAsState(null).value ?: return emptyList()
            val objectives = repositories.wvw.selectedMatchObjectives().collectAsState(emptyList()).value

            val borderlands = match.maps.filter { map ->
                map.type.enumValueOrNull().isOneOf(WvwMapType.BLUE_BORDERLANDS, WvwMapType.RED_BORDERLANDS, WvwMapType.GREEN_BORDERLANDS)
            }

            return borderlands.mapNotNull { borderland ->
                val matchRuins = borderland.objectives.filter { objective -> objective.type.enumValueOrNull() == WvwObjectiveType.RUINS }
                if (matchRuins.isEmpty()) {
                    Logger.w { "There are no ruins on map ${borderland.id}." }
                    return@mapNotNull null
                }

                val objectiveRuins = matchRuins.mapNotNull { matchRuin ->
                    objectives.firstOrNull { objective -> objective.id == matchRuin.id }
                }

                if (objectiveRuins.size != matchRuins.size) {
                    Logger.w { "Mismatch between the number of ruins in the match and objectives on map ${borderland.id}." }
                    return@mapNotNull null
                }

                // Use the center of all of the ruins as the position of the bloodlust icon.
                val x = objectiveRuins.sumOf { ruin -> ruin.coordinates.x } / objectiveRuins.size
                val y = objectiveRuins.sumOf { ruin -> ruin.coordinates.y } / objectiveRuins.size

                // Scale the position before using it.
                // TODO remove from config
                val width = configuration.wvw.bloodlust.size.width
                val height = configuration.wvw.bloodlust.size.height
                val coordinates = Point2D(x, y).scaledCoordinates(width, height)
                val bonus = borderland.bonuses.firstOrNull { bonus -> bonus.type.enumValueOrNull() == WvwMapBonusType.BLOODLUST }
                val owner = bonus?.owner?.enumValueOrNull() ?: WvwObjectiveOwner.NEUTRAL
                Bloodlust(
                    link = configuration.wvw.bloodlust.iconLink.asImageUrl(),
                    color = configuration.wvw.color(owner = owner),
                    x = coordinates.x.toInt(),
                    y = coordinates.y.toInt(),
                    width = width,
                    height = height,
                    description = owner.stringResource(), // TODO include word bloodlust
                    enabled = configuration.wvw.bloodlust.enabled
                )
            }
        }

    val objectives: Collection<Objective>
        @Composable
        get() {
            val match = repositories.wvw.selectedMatch().collectAsState(null).value ?: return emptyList()
            val objectives = repositories.wvw.selectedMatchObjectives().collectAsState(emptyList()).value
            val upgrades = repositories.wvw.selectedMatchUpgrades().collectAsState(emptyList()).value
            val guildUpgrades = repositories.wvw.selectedMatchGuildUpgrades().collectAsState(emptyList()).value

            val models = objectives.mapNotNull { objective ->
                val fromConfig = configuration.wvw.objective(objective)
                val fromMatch = match.objective(objective) ?: return@mapNotNull null
                val upgrade = upgrades.firstOrNull { upgrade -> upgrade.id == objective.upgradeId }
                val tiers = upgrade?.tiers(fromMatch.yaksDelivered)?.flatMap { tier -> tier.upgrades } ?: emptyList()

                // TODO remove from config
                val size = fromConfig?.size ?: configuration.wvw.objectives.defaultSize
                val coordinates = objective.position().scaledCoordinates(size.width, size.height)

                // Get the progression level associated with the current number of yaks delivered to the objective.
                val progression = upgrade?.let {
                    val level = upgrade.level(fromMatch.yaksDelivered)
                    configuration.wvw.objectives.progressions.progression.getOrNull(level)
                }

                // See if any of the progressed tiers has a permanent waypoint upgrade, or the tactic for the temporary waypoint.
                val hasWaypointUpgrade = tiers.any { tier -> configuration.wvw.objectives.waypoint.upgradeNameRegex.matches(tier.name) }
                val hasWaypointTacticUpgrade = fromMatch.guildUpgradeIds.mapNotNull { id ->
                    guildUpgrades.firstOrNull { guildUpgrade -> id == guildUpgrade.id }
                }.any { tactic -> configuration.wvw.objectives.waypoint.guild.upgradeNameRegex.matches(tactic.name) }
                val hasWaypointTactic = configuration.wvw.objectives.waypoint.guild.enabled && hasWaypointTacticUpgrade

                Objective(
                    objective = objective,
                    x = coordinates.x.toInt(),
                    y = coordinates.y.toInt(),
                    width = size.width,
                    height = size.height,

                    // Use a default link when the icon link doesn't exist. The link won't exist for atypical types such as Spawn/Mercenary.
                    link = objective.iconLink.value.ifBlank { fromConfig?.defaultIconLink ?: "" }.asImageUrl(),

                    // TODO translate
                    description = StringDesc.Raw(objective.name),
                    color = configuration.wvw.color(fromMatch),
                    progression = ObjectiveProgression(
                        enabled = configuration.wvw.objectives.progressions.enabled && progression != null,
                        description = StringDesc.Raw(""), // TODO translation
                        link = progression?.indicatorLink?.asImageUrl(),
                        width = configuration.wvw.objectives.progressions.indicatorSize.width,
                        height = configuration.wvw.objectives.progressions.indicatorSize.height,
                    ),
                    claim = ObjectiveClaim(
                        enabled = configuration.wvw.objectives.claim.enabled && !fromMatch.claimedBy?.value.isNullOrBlank(),
                        description = StringDesc.Raw(""), // TODO translation
                        link = configuration.wvw.objectives.claim.iconLink?.asImageUrl(),
                        width = configuration.wvw.objectives.claim.size.width,
                        height = configuration.wvw.objectives.claim.size.height
                    ),
                    waypoint = ObjectiveWaypoint(
                        enabled = configuration.wvw.objectives.waypoint.enabled && (hasWaypointUpgrade || hasWaypointTactic),
                        link = configuration.wvw.objectives.waypoint.iconLink?.asImageUrl(),
                        description = StringDesc.Raw(""), // TODO translation
                        width = configuration.wvw.objectives.waypoint.size.width,
                        height = configuration.wvw.objectives.waypoint.size.height,
                        color = if (hasWaypointTactic && !hasWaypointUpgrade) Hex(configuration.wvw.objectives.waypoint.guild.color).color() else null
                    ),
                    immunity = ObjectiveImmunity(
                        enabled = configuration.wvw.objectives.immunity.enabled,
                        duration = fromConfig?.immunity ?: configuration.wvw.objectives.immunity.defaultDuration,
                        startTime = fromMatch.lastFlippedAt,
                    )
                )
            }

            // Render from bottom right to top left so that overlap is consistent.
            val comparator = compareByDescending<Objective> { objective -> objective.y }.thenByDescending { objective -> objective.x }
            return models.sortedWith(comparator)
        }

    /**
     * The objective selected by the user on the map.
     */
    val selected = mutableStateOf<WvwObjective?>(null)
    val selectedObjective: SelectedObjective?
        @Composable
        get() = run {
            val objective = selected.value ?: return@run null
            val fromMatch = repositories.wvw.selectedMatch().collectAsState(null).value.objective(objective)
            val owner = fromMatch?.owner?.enumValueOrNull() ?: WvwObjectiveOwner.NEUTRAL
            SelectedObjective(
                // TODO translate
                title = objective.name.desc() + " (".desc() + owner.stringResource() + objective.type.value.desc() + ")".desc(),
                subtitle = fromMatch?.lastFlippedAt?.let { lastFlippedAt ->
                    "Flipped at ${configuration.wvw.selectedDateFormatted(lastFlippedAt)}"
                }?.desc()
            )
        }

    /**
     * @return the scaled coordinates of the image
     */
    @Composable
    private fun Point2D.scaledCoordinates(width: Number, height: Number): Point2D {
        // Scale the objective coordinates to the zoom level and remove excluded bounds.
        val scaled = grid.value.scale(x.toInt(), y.toInt())

        // Displace the coordinates so that it aligns with the center of the image.
        return Point2D(
            x = scaled.first - width.toDouble() / 2,
            y = scaled.second - height.toDouble() / 2
        )
    }
}