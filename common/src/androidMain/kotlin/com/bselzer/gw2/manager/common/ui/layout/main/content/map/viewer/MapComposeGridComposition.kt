package com.bselzer.gw2.manager.common.ui.layout.main.content.map.viewer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ViewerViewModel
import com.bselzer.ktx.value.identifier.Identifier
import com.bselzer.ktx.value.identifier.identifier
import ovh.plrapps.mapcompose.api.*
import ovh.plrapps.mapcompose.core.TileStreamProvider
import ovh.plrapps.mapcompose.ui.MapUI
import ovh.plrapps.mapcompose.ui.state.MapState
import java.io.ByteArrayInputStream

/**
 * The composition for laying out the grid of tiles using the MapCompose library.
 */
class MapComposeGridComposition(model: ViewerViewModel) : GridComposition(model) {
    @Composable
    override fun ViewerViewModel.Content(modifier: Modifier) {
        val state = mapState.also { state -> GridEffects(state) }
        MapUI(
            modifier = Modifier.fillMaxSize().then(modifier),
            state = state
        )
    }

    @Composable
    private fun ViewerViewModel.GridEffects(state: MapState) {
        LaunchedEffect(grid) {
            state.removeAllLayers()
            state.addLayer(tileStreamProvider)
        }

        LaunchedEffect(objectiveIcons, bloodlusts) {
            state.removeAllMarkers()

            objectiveIcons.forEach { objective ->
                val (width, height) = objectiveSize
                val (normalizedX, normalizedY) = grid.boundedNormalizeAbsolutePosition(objective.x, objective.y)
                state.addIdentifiableMarker(
                    id = objective.objective.id,
                    x = normalizedX,
                    y = normalizedY,
                    absoluteOffset = Offset(-width / 2f, -height / 2f),
                ) {
                    objective.Objective(Modifier)
                }
            }

            bloodlusts.forEach { bloodlust ->
                val (width, height) = bloodlustSize
                val (normalizedX, normalizedY) = grid.boundedNormalizeAbsolutePosition(bloodlust.x, bloodlust.y)
                state.addIdentifiableMarker(
                    id = bloodlust.link.toString().identifier(),
                    x = normalizedX,
                    y = normalizedY,
                    absoluteOffset = Offset(-width / 2f, -height / 2f),
                ) {
                    bloodlust.Bloodlust(Modifier)
                }
            }
        }
    }

    private val tileStreamProvider = model.run {
        TileStreamProvider { y, x, zoom ->
            val boundedX = grid.startX + x
            val boundedY = grid.startY + y

            // NOTE: Must use the grid zoom since we are treating each zoom level as its own map and not using levels.
            val tile = request(grid.getTileOrDefault(boundedX, boundedY, grid.zoom))
            ByteArrayInputStream(tile.content)
        }
    }

    private val mapState: MapState
        @Composable
        get() = model.run {
            val (scrollX, scrollY) = scrollToRegionCoordinates
            remember(grid.width, grid.height) {
                /**
                 * NOTE: treating each zoom level as its own map since the actual map contents need to be bounded
                 *  without the bounds, there would otherwise be a lot of blank space as zoom levels are increased
                 */
                MapState(
                    levelCount = 1,
                    fullWidth = grid.width,
                    fullHeight = grid.height,
                    tileSize = grid.tileWidth,
                    initialValuesBuilder = {
                        val (normalizedX, normalizedY) = grid.normalizeAbsolutePosition(scrollX, scrollY)
                        scroll(normalizedX, normalizedY)
                    }
                ).apply {
                    addLayer(tileStreamProvider)

                    // Unlike a normal map, rotation does not provide much value and accidentally rotating would be more of a likely hindrance.
                    disableRotation()
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
     */
    private fun MapState.addIdentifiableMarker(
        id: Identifier<String> = counterId,
        x: Double,
        y: Double,
        relativeOffset: Offset = Offset.Zero,
        absoluteOffset: Offset,
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
        c = c,
    )
}