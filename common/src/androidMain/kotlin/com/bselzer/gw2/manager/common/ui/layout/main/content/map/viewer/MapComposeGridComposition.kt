package com.bselzer.gw2.manager.common.ui.layout.main.content.map.viewer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ViewerViewModel
import com.bselzer.gw2.v2.tile.model.position.GridPosition
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
                val normalized = grid.normalize(objective.position)
                state.addIdentifiableMarker(
                    id = objective.objective.id,
                    x = normalized.x,
                    y = normalized.y,
                    absoluteOffset = Offset(-width / 2f, -height / 2f),
                ) {
                    objective.Objective(Modifier)
                }
            }

            bloodlusts.forEach { bloodlust ->
                val (width, height) = bloodlustSize
                val normalized = grid.normalize(bloodlust.position)
                state.addIdentifiableMarker(
                    id = bloodlust.link.toString().identifier(),
                    x = normalized.x,
                    y = normalized.y,
                    absoluteOffset = Offset(-width / 2f, -height / 2f),
                ) {
                    bloodlust.Bloodlust(Modifier)
                }
            }
        }
    }

    private val tileStreamProvider = model.run {
        TileStreamProvider { y, x, zoom ->
            val bounded = GridPosition(x = grid.topLeft.x + x, y = grid.topLeft.y + y)

            // NOTE: Must use the grid zoom since we are treating each zoom level as its own map and not using levels.
            val tile = request(grid.getTileOrDefault(bounded, grid.zoom))
            if (tile.content.isEmpty()) {
                null
            } else {
                ByteArrayInputStream(tile.content)
            }
        }
    }

    private val mapState: MapState
        @Composable
        get() = model.run {
            val normalized = grid.normalize(scrollToRegionCoordinates)
            remember(grid.size, grid.tileSize) {
                /**
                 * NOTE: treating each zoom level as its own map since the actual map contents need to be bounded
                 *  without the bounds, there would otherwise be a lot of blank space as zoom levels are increased
                 */
                MapState(
                    levelCount = 1,
                    fullWidth = grid.size.width.toInt(),
                    fullHeight = grid.size.height.toInt(),

                    // NOTE: size must be at least one to avoid exception upon bitmap creation
                    tileSize = grid.tileSize.width.toInt().coerceAtLeast(1),
                    initialValuesBuilder = {
                        scroll(normalized.x, normalized.y)
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