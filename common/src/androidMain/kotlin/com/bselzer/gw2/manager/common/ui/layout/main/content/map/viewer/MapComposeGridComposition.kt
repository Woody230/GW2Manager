package com.bselzer.gw2.manager.common.ui.layout.main.content.map.viewer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.constraintlayout.compose.ConstraintLayout
import com.bselzer.gw2.manager.common.ui.layout.image.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.image.Content
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ViewerViewModel
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
                val width = 64
                val height = 64
                val (normalizedX, normalizedY) = grid.boundedNormalizeAbsolutePosition(objective.x, objective.y)
                state.addMarker(
                    id = objective.objective.id.value,
                    x = normalizedX,
                    y = normalizedY,
                    relativeOffset = Offset.Zero,
                    absoluteOffset = Offset(-width / 2f, -height / 2f),
                    clipShape = null,
                    clickable = false,
                ) {
                    ConstraintLayout {
                        val (icon, timer, upgradeIndicator, claimIndicator, waypointIndicator) = createRefs()

                        // Overlay the objective image onto the map image.
                        objective.Image(
                            width = width,
                            height = height,
                            modifier = Modifier.constrainAs(icon) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                            }
                        )

                        objective.progression.Progression(
                            modifier = Modifier.constrainAs(upgradeIndicator) {
                                // Display the indicator in the top center of the objective icon.
                                top.linkTo(icon.top)
                                start.linkTo(icon.start)
                                end.linkTo(icon.end)
                            },
                        )

                        objective.claim.Claim(
                            modifier = Modifier.constrainAs(claimIndicator) {
                                // Display the indicator in the bottom right of the objective icon.
                                bottom.linkTo(icon.bottom)
                                end.linkTo(icon.end)
                            }
                        )

                        objective.waypoint.Waypoint(
                            modifier = Modifier.constrainAs(waypointIndicator) {
                                // Display the indicator in the bottom left of the objective icon.
                                bottom.linkTo(icon.bottom)
                                start.linkTo(icon.start)
                            },
                        )

                        objective.immunity.ImmunityTimer(
                            modifier = Modifier.constrainAs(timer) {
                                // Display the timer underneath the objective icon.
                                top.linkTo(icon.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                        )
                    }
                }
            }

            bloodlusts.forEach { bloodlust ->
                val width = 64
                val height = 64
                val (normalizedX, normalizedY) = grid.boundedNormalizeAbsolutePosition(bloodlust.x, bloodlust.y)
                state.addMarker(
                    id = bloodlust.link.toString(),
                    x = normalizedX,
                    y = normalizedY,
                    relativeOffset = Offset.Zero,
                    absoluteOffset = Offset(-width / 2f, -height / 2f),
                    clipShape = null,
                    clickable = false,
                ) {
                    with(bloodlust) {
                        AsyncImage(
                            image = link,
                            width = width,
                            height = height,
                            color = color,
                            description = description
                        ).Content()
                    }
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
}