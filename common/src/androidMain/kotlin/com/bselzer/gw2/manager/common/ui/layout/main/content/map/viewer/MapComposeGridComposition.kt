package com.bselzer.gw2.manager.common.ui.layout.main.content.map.viewer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import com.arkivanov.essenty.lifecycle.doOnPause
import com.bselzer.gw2.manager.common.ui.layout.custom.indicator.viewmodel.DetailedIconViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.BloodlustViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.MapLabelViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ViewerViewModel
import com.bselzer.gw2.v2.model.tile.position.BoundedPosition
import com.bselzer.gw2.v2.model.tile.position.GridPosition
import com.bselzer.ktx.compose.ui.unit.toDp
import com.bselzer.ktx.compose.ui.unit.toPx
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.settings.safeState
import com.bselzer.ktx.value.identifier.Identifier
import com.bselzer.ktx.value.identifier.identifier
import kotlinx.coroutines.launch
import ovh.plrapps.mapcompose.api.*
import ovh.plrapps.mapcompose.core.TileStreamProvider
import ovh.plrapps.mapcompose.ui.MapUI
import ovh.plrapps.mapcompose.ui.state.MapState
import ovh.plrapps.mapcompose.ui.state.markers.model.RenderingStrategy
import java.io.ByteArrayInputStream

/**
 * The composition for laying out the grid of tiles using the MapCompose library.
 */
@OptIn(ExperimentalClusteringApi::class)
class MapComposeGridComposition(model: ViewerViewModel) : GridComposition(model) {
    private companion object {
        const val lazyLoaderId = "default"

        // Objectives should be highest prioritized.
        const val objectivePriority = 10f

        // Bloodlusts are of similar priority to objectives.
        const val bloodlustPriority = objectivePriority - 1
    }


    @Composable
    override fun ViewerViewModel.Content(modifier: Modifier) {
        mapState?.let { state ->
            LifecycleEffects(state)
            GridEffects(state)

            MapUI(
                modifier = Modifier.fillMaxSize().then(modifier),
                state = state
            )
        }
    }

    @Composable
    private fun ViewerViewModel.LifecycleEffects(state: MapState) {
        LaunchedEffect(state) {
            lifecycle.doOnPause(isOneTime = true) {
                scope.launch {
                    Logger.d { "Grid | Scroll | Saving as [${state.scroll.x},${state.scroll.y}]." }
                    horizontalScroll.scrollTo(state.scroll.x.toInt())
                    verticalScroll.scrollTo(state.scroll.y.toInt())
                }
            }
        }
    }

    @Composable
    private fun ViewerViewModel.GridEffects(state: MapState) {
        val objectiveWidth = objectiveSize.width.toPx()
        val objectiveHeight = objectiveSize.height.toPx()
        val shouldShowMapLabel = preferences.wvw.showMapLabel.safeState().value
        LaunchedEffect(state, objectiveIcons, bloodlustIcons, mapLabels) {
            Logger.d { "Grid | UI | Adding ${objectiveIcons.size} objectives, ${bloodlustIcons.size} bloodlusts, and ${mapLabels.size} map labels." }

            state.removeAllMarkers()

            objectiveIcons.forEach { objective -> Objective(objective, state, objectiveWidth, objectiveHeight) }
            bloodlustIcons.forEach { bloodlust -> Bloodlust(bloodlust, state) }

            if (shouldShowMapLabel) {
                mapLabels.forEach { label -> MapLabel(label, state) }
            }
        }
    }

    private fun ViewerViewModel.Objective(objective: DetailedIconViewModel, state: MapState, width: Float, height: Float) {
        val normalized = grid.normalize(objective.position)
        state.addIdentifiableMarker(
            x = normalized.x,
            y = normalized.y,
            zIndex = objectivePriority,

            // Displace the coordinates so that it aligns with the center of the image.
            // Not using relative offset because the timer coming in/out of visibility will push the objective.
            absoluteOffset = Offset(-width / 2f, -height / 2f),
        ) {
            objective.Objective(Modifier)
        }
    }

    private fun ViewerViewModel.Bloodlust(bloodlust: BloodlustViewModel, state: MapState) {
        val normalized = grid.normalize(bloodlust.position)
        state.addIdentifiableMarker(
            x = normalized.x,
            y = normalized.y,
            zIndex = bloodlustPriority,

            // Displace the coordinates so that it aligns with the center of the image.
            relativeOffset = Offset(-0.5f, -0.5f)
        ) {
            bloodlust.Bloodlust(Modifier)
        }
    }

    private fun ViewerViewModel.MapLabel(label: MapLabelViewModel, state: MapState) {
        val normalized = grid.normalize(label.position)
        state.addIdentifiableMarker(
            x = normalized.x,
            y = normalized.y,

            // Provide a buffer between the top of the map and the label.
            // This is particularly needed for red borderlands where the top camp will overlap with the text normally.
            relativeOffset = Offset(0f, -1f),
        ) {
            label.Label(Modifier)
        }
    }

    private val tileStreamProvider = model.run {
        TileStreamProvider { y, x, zoom ->
            try {
                val bounded = GridPosition(x = grid.topLeft.x + x, y = grid.topLeft.y + y)

                // NOTE: Must use the grid zoom since we are treating each zoom level as its own map and not using levels.
                val tile = request(grid.getTileOrDefault(bounded, grid.zoom))
                if (tile.content.isEmpty()) {
                    null
                } else {
                    ByteArrayInputStream(tile.content)
                }
            } catch (ex: Exception) {
                // May be in the process of releasing the database, which will cause an exception to be thrown.
                Logger.e(ex) { "Failed to get the tile at [$x, $y] for zoom level ${grid.zoom}." }
                null
            }
        }
    }

    private val mapState: MapState?
        @Composable
        get() = model.run {
            // Use the coordinates saved from leaving this screen if they exist.
            // Otherwise do the typical scrolling to the configured region.
            // NOTE: currently ignoring scroll enable since it will only be triggered on map change which is fine
            val coordinates = if (horizontalScroll.value > 0 || verticalScroll.value > 0) {
                BoundedPosition(horizontalScroll.value.toDouble(), verticalScroll.value.toDouble())
            } else {
                scrollToRegionCoordinates
            }

            val width = grid.size.width.toInt()
            val height = grid.size.height.toInt()
            val tileWidth = grid.tileSize.width.toInt()

            // NOTE: coercing a defaulted grid with a min of 1 will cause an exception so just wait for the grid to be properly initialized
            if (width <= 0 || height <= 0 || tileWidth <= 0) {
                return@run null
            }

            val lazyLoaderPadding = width.toDp()
            remember(grid.size, grid.tileSize) {
                /**
                 * NOTE: treating each zoom level as its own map since the actual map contents need to be bounded
                 *  without the bounds, there would otherwise be a lot of blank space as zoom levels are increased
                 */
                MapState(
                    levelCount = 1,
                    fullWidth = width,
                    fullHeight = height,

                    // NOTE: size must be at least one to avoid exception upon bitmap creation
                    tileSize = tileWidth,
                    initialValuesBuilder = {
                        val normalized = grid.normalize(coordinates)
                        scroll(normalized.x, normalized.y, Offset.Zero)
                    }
                ).apply {
                    // Unlike a normal map, rotation does not provide much value and accidentally rotating would be more of a likely hindrance.
                    disableRotation()

                    addLazyLoader(lazyLoaderId, padding = lazyLoaderPadding)
                    setPreloadingPadding(padding = tileWidth)

                    onTap { x, y ->
                        // Clear the objective pop-up.
                        model.selected.value = null
                    }

                    addLayer(tileStreamProvider)
                }
            }
        }

    private var _counter: Int = Int.MIN_VALUE
    private val counterId: Identifier<String>
        get() = synchronized(this) {
            val id = _counter
            _counter += 1
            return id.toString().identifier()
        }

    /**
     * Add a marker to the map, with defaults more reasonable for our purposes:
     *
     * No relative offset and a required absolute offset.
     * The absolute offset should be half the size of the icon so that the icon is kept centered around its coordinates.
     *
     * Clickable is false since the marker can just handle its own clickable and not use the map state.
     *
     * Clip shape is null since clipping interferes with key components such as the timers.
     *
     * Lazy loading rendering strategy instead of eagerly rendering the marker.
     */
    private fun MapState.addIdentifiableMarker(
        id: Identifier<String> = counterId,
        x: Double,
        y: Double,
        relativeOffset: Offset = Offset.Zero,
        absoluteOffset: Offset = Offset.Zero,
        zIndex: Float = 0f,
        clickable: Boolean = false,
        clipShape: Shape? = null,
        isConstrainedInBounds: Boolean = true,
        c: @Composable () -> Unit
    ) = addMarker(
        id = id.value,
        x = x,
        y = y,
        relativeOffset = relativeOffset,
        absoluteOffset = absoluteOffset,
        zIndex = zIndex,
        clickable = clickable,
        clipShape = clipShape,
        isConstrainedInBounds = isConstrainedInBounds,
        renderingStrategy = RenderingStrategy.LazyLoading(lazyLoaderId),
        c = c,
    )
}