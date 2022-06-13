package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.essenty.lifecycle.subscribe
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.AppBarAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.GeneralAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.map.viewer.*
import com.bselzer.gw2.v2.model.enumeration.WvwMapBonusType
import com.bselzer.gw2.v2.model.enumeration.WvwMapType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveType
import com.bselzer.gw2.v2.model.enumeration.extension.enumValueOrNull
import com.bselzer.gw2.v2.model.extension.wvw.*
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.gw2.v2.resource.strings.stringDesc
import com.bselzer.gw2.v2.tile.model.position.BoundedPosition
import com.bselzer.gw2.v2.tile.model.position.TexturePosition
import com.bselzer.ktx.compose.resource.ui.layout.icon.zoomInMapIconInteractor
import com.bselzer.ktx.compose.resource.ui.layout.icon.zoomOutMapIconInteractor
import com.bselzer.ktx.compose.ui.graphics.color.Hex
import com.bselzer.ktx.compose.ui.graphics.color.color
import com.bselzer.ktx.function.objects.isOneOf
import com.bselzer.ktx.logging.Logger
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.asImageUrl
import dev.icerock.moko.resources.format
import kotlinx.coroutines.launch

class ViewerViewModel(
    context: AppComponentContext,
    showDialog: (DialogConfig) -> Unit,
) : MapViewModel(context, showDialog) {
    init {
        if (configuration.wvw.map.refreshGrid) {
            lifecycle.subscribe(
                onResume = { refreshGrid = true },
                onPause = { refreshGrid = false },
                onDestroy = { refreshGrid = false },
                onStop = { refreshGrid = false }
            )
        }
    }

    override val title: StringDesc = Gw2Resources.strings.map.desc()

    // TODO on refresh action also refresh the grid
    private val zoomInAction
        get() = GeneralAction(
            enabled = zoom < zoomRange.last,
            icon = { zoomInMapIconInteractor() },
            onClick = { changeZoom(increment = 1) }
        )

    private val zoomOutAction
        get() = GeneralAction(
            enabled = zoom > zoomRange.first,
            icon = { zoomOutMapIconInteractor() },
            onClick = { changeZoom(increment = -1) }
        )

    override val actions: List<AppBarAction>
        get() = super.actions + listOf(zoomInAction, zoomOutAction)

    /**
     * Updates the zoom to be within the configured range.
     */
    suspend fun changeZoom(increment: Int) = updateZoom(zoom + increment)

    val horizontalScroll: ScrollState = ScrollState(0)
    val verticalScroll: ScrollState = ScrollState(0)

    /**
     * Whether to scroll the map to the configured region.
     */
    private val shouldScrollToRegion = mutableStateOf(configuration.wvw.map.scrollEnabled)

    /**
     * The coordinates to scroll to for the configured map.
     */
    val scrollToRegionCoordinates: BoundedPosition
        @Composable
        get() {
            val map = continentMaps.values.firstOrNull { map -> map.name == configuration.wvw.map.scrollTo } ?: return BoundedPosition()
            return grid.bounded(map.continentRectangle.topLeft)
        }

    /**
     * Scrolls the map to the configured WvW map within the grid.
     */
    @Composable
    fun scrollToRegion() {
        if (shouldScrollToRegion.value) {
            val coordinates = scrollToRegionCoordinates
            rememberCoroutineScope().launch {
                horizontalScroll.animateScrollTo(coordinates.x.toInt())
                verticalScroll.animateScrollTo(coordinates.y.toInt())
                shouldScrollToRegion.value = false
            }
        }
    }

    val mapLabels: Collection<MapLabel>
        get() = matchMaps.map { (wvwMap, map) ->
            val type = wvwMap.type.enumValueOrNull()
            val owner = type?.owner() ?: WvwObjectiveOwner.NEUTRAL
            MapLabel(
                color = configuration.wvw.color(owner),
                position = grid.bounded(map.continentRectangle.topLeft),
                width = map.continentRectangle.topRight.x - map.continentRectangle.topLeft.x,
                description = when {
                    // If there are worlds then display them.
                    match?.linkedWorlds(owner)?.isNotEmpty() == true -> displayableLinkedWorlds(owner)

                    // Otherwise fall back to the map name.
                    type != null -> type.stringDesc()
                    else -> map.name.translated().desc()
                }
            )
        }

    val bloodlusts: Collection<Bloodlust>
        get() {
            val match = match ?: return emptyList()
            val borderlands = match.maps.filter { map ->
                map.type.enumValueOrNull().isOneOf(WvwMapType.BLUE_BORDERLANDS, WvwMapType.RED_BORDERLANDS, WvwMapType.GREEN_BORDERLANDS)
            }

            return borderlands.mapNotNull { borderland ->
                val matchRuins = borderland.objectives.filter { objective -> objective.type.enumValueOrNull() == WvwObjectiveType.RUINS }
                if (matchRuins.isEmpty()) {
                    Logger.w { "There are no ruins on map ${borderland.id}." }
                    return@mapNotNull null
                }

                val objectiveRuins = matchRuins.mapNotNull { matchRuin -> objectives[matchRuin.id] }
                if (objectiveRuins.size != matchRuins.size) {
                    Logger.w { "Mismatch between the number of ruins in the match and objectives on map ${borderland.id}." }
                    return@mapNotNull null
                }

                // Use the center of all of the ruins as the position of the bloodlust icon.
                val x = objectiveRuins.sumOf { ruin -> ruin.coordinates.x } / objectiveRuins.size
                val y = objectiveRuins.sumOf { ruin -> ruin.coordinates.y } / objectiveRuins.size
                val position = TexturePosition(x, y)

                val bonus = borderland.bonuses.firstOrNull { bonus -> bonus.type.enumValueOrNull() == WvwMapBonusType.BLOODLUST }
                val owner = bonus?.owner?.enumValueOrNull() ?: WvwObjectiveOwner.NEUTRAL
                Bloodlust(
                    link = configuration.wvw.bloodlust.iconLink.asImageUrl(),
                    color = configuration.wvw.color(owner = owner),
                    description = AppResources.strings.bloodlust_for.format(owner.stringDesc()),

                    // Scale the coordinates to the zoom level and remove excluded bounds.
                    position = grid.bounded(position)
                )
            }
        }

    val objectiveIcons: Collection<ObjectiveIcon>
        get() {
            val match = match ?: return emptyList()
            val models = objectives.values.mapNotNull { objective ->
                val fromConfig = configuration.wvw.objective(objective)
                val fromMatch = match.objective(objective) ?: return@mapNotNull null
                val upgrade = upgrades[objective.upgradeId]
                val tiers = upgrade?.tiers(fromMatch.yaksDelivered)?.flatMap { tier -> tier.upgrades } ?: emptyList()

                val position = objective.position()

                // Get the progression level associated with the current number of yaks delivered to the objective.
                val level = upgrade?.level(fromMatch.yaksDelivered)
                val progression = level?.let { configuration.wvw.objectives.progressions.getOrNull(level) }

                // See if any of the progressed tiers has a permanent waypoint upgrade, or the tactic for the temporary waypoint.
                val hasWaypointUpgrade = tiers.any { tier -> configuration.wvw.objectives.waypoint.upgradeNameRegex.matches(tier.name) }
                val hasWaypointTactic = fromMatch.guildUpgradeIds.mapNotNull { id -> guildUpgrades[id] }
                    .any { tactic -> configuration.wvw.objectives.waypoint.guild.upgradeNameRegex.matches(tactic.name) }

                ObjectiveIcon(
                    objective = objective,

                    // Scale the objective coordinates to the zoom level and remove excluded bounds.
                    position = grid.bounded(position),

                    // Use a default link when the icon link doesn't exist. The link won't exist for atypical types such as Spawn/Mercenary.
                    link = objective.iconLink.value.ifBlank { fromConfig?.defaultIconLink ?: "" }.asImageUrl(),

                    description = objective.name.translated().desc(),
                    color = configuration.wvw.color(fromMatch),
                    progression = ObjectiveProgression(
                        enabled = progression != null,
                        description = level?.let { AppResources.strings.upgrade_level.format(level) },
                        link = progression?.indicatorLink?.asImageUrl(),
                    ),
                    claim = ObjectiveClaim(
                        enabled = !fromMatch.claimedBy?.value.isNullOrBlank(),
                        description = AppResources.strings.claimed.desc(),
                        link = configuration.wvw.objectives.claim.iconLink?.asImageUrl(),
                    ),
                    waypoint = ObjectiveWaypoint(
                        enabled = hasWaypointUpgrade || hasWaypointTactic,
                        link = configuration.wvw.objectives.waypoint.iconLink?.asImageUrl(),
                        description = when {
                            hasWaypointUpgrade -> AppResources.strings.permanent_waypoint.desc()
                            hasWaypointTactic -> AppResources.strings.temporary_waypoint.desc()
                            else -> null
                        },
                        color = if (hasWaypointTactic && !hasWaypointUpgrade) Hex(configuration.wvw.objectives.waypoint.guild.color).color() else null
                    ),
                    immunity = ObjectiveImmunity(
                        duration = fromConfig?.immunity,
                        startTime = fromMatch.lastFlippedAt,
                    )
                )
            }

            // Render from bottom right to top left so that overlap is consistent.
            val comparator = compareByDescending<ObjectiveIcon> { objective -> objective.position.y }.thenByDescending { objective -> objective.position.x }
            return models.sortedWith(comparator)
        }

    /**
     * The objective selected by the user on the map.
     */
    val selected = mutableStateOf<WvwObjective?>(null)
    val selectedObjective: SelectedObjective?
        get() = run {
            val objective = selected.value ?: return@run null
            val fromMatch = match.objective(objective)
            val owner = fromMatch?.owner?.enumValueOrNull() ?: WvwObjectiveOwner.NEUTRAL
            val type = objective.type.enumValueOrNull() ?: WvwObjectiveType.GENERIC
            val name = objective.name.translated()
            SelectedObjective(
                title = AppResources.strings.selected_objective.format(name.desc(), owner.stringDesc(), type.stringDesc()),
                subtitle = fromMatch?.lastFlippedAt?.let { lastFlippedAt ->
                    configuration.wvw.flippedAt(lastFlippedAt)
                }
            )
        }
}