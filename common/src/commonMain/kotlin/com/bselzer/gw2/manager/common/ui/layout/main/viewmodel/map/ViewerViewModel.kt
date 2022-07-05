package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.essenty.lifecycle.subscribe
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.custom.indicator.viewmodel.DetailedIconViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.AppBarAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.GeneralAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.map.viewer.SelectedObjective
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveType
import com.bselzer.gw2.v2.model.enumeration.extension.decodeOrNull
import com.bselzer.gw2.v2.model.extension.wvw.objective
import com.bselzer.gw2.v2.model.extension.wvw.position
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.gw2.v2.resource.strings.stringDesc
import com.bselzer.gw2.v2.tile.model.position.BoundedPosition
import com.bselzer.ktx.compose.resource.ui.layout.icon.zoomInMapIconInteractor
import com.bselzer.ktx.compose.resource.ui.layout.icon.zoomOutMapIconInteractor
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
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

    val mapLabels: Collection<MapLabelViewModel>
        get() = matchMaps.map { (wvwMap, map) ->
            MapLabelViewModel(
                context = this,
                wvwMap = wvwMap,
                map = map
            )
        }

    val bloodlustIcons: Collection<BloodlustViewModel>
        get() {
            val borderlands = match.maps.filter { map -> mapTypes.contains(map.type.decodeOrNull()) }
            return borderlands.map { borderland -> BloodlustViewModel(context = this, borderland = borderland) }.filter { bloodlust -> bloodlust.exists }
        }

    // Render from bottom right to top left so that overlap is consistent.
    private val comparator = compareByDescending<WvwObjective> { objective -> objective.position().y }.thenByDescending { objective -> objective.position().y }
    val objectiveIcons: List<DetailedIconViewModel>
        get() = objectives.values.sortedWith(comparator).mapNotNull { objective ->
            val matchObjective = match.objective(objective) ?: return@mapNotNull null
            DetailedIconViewModel(
                context = this,
                objective = objective,
                matchObjective = matchObjective,
                upgrade = upgrades[objective.upgradeId],

                // Scale the objective coordinates to the zoom level and remove excluded bounds.
                position = grid.bounded(objective.position())
            )
        }

    /**
     * The objective selected by the user on the map.
     */
    val selected = mutableStateOf<WvwObjective?>(null)
    val selectedObjective: SelectedObjective?
        get() = run {
            val objective = selected.value ?: return@run null
            val fromMatch = match.objective(objective)
            val owner = fromMatch?.owner?.decodeOrNull() ?: WvwObjectiveOwner.NEUTRAL
            val type = objective.type.decodeOrNull() ?: WvwObjectiveType.GENERIC
            val name = objective.name.translated()
            SelectedObjective(
                title = AppResources.strings.selected_objective.format(name.desc(), owner.stringDesc(), type.stringDesc()),
                subtitle = fromMatch?.lastFlippedAt?.let { lastFlippedAt ->
                    configuration.wvw.flippedAt(lastFlippedAt)
                }
            )
        }
}