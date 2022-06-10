package com.bselzer.gw2.manager.common.ui.layout.main.content.map

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.arkivanov.decompose.router.bringToFront
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.image.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.image.Content
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MapConfig
import com.bselzer.gw2.manager.common.ui.layout.main.model.map.viewer.*
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ViewerViewModel
import com.bselzer.gw2.v2.tile.model.response.Tile
import com.bselzer.ktx.compose.image.ui.layout.asImageBitmap
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.background.image.BackgroundImage
import com.bselzer.ktx.compose.ui.unit.toDp
import com.bselzer.ktx.datetime.format.minuteFormat
import kotlinx.coroutines.launch
import kotlin.time.Duration

class ViewerComposition(model: ViewerViewModel) : ViewModelComposition<ViewerViewModel>(model) {
    @Composable
    override fun ViewerViewModel.Content() {
        // Intentionally not using a background in case the device size is large enough that the map doesn't match the full size.

        val scope = rememberCoroutineScope()
        val pinchToZoom = rememberTransformableState { zoomChange, panChange, rotationChange ->
            // Allow the user to change the zoom by pinching the map.
            val change = if (zoomChange > 1) 1 else -1
            scope.launch { changeZoom(change) }
        }

        ConstraintLayout(
            modifier = Modifier.fillMaxSize().transformable(pinchToZoom)
        ) {
            val (map, selectedObjective) = createRefs()
            GridData(Modifier.constrainAs(map) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })

            // Overlay the selected objective over everything else on the map.
            SelectedObjectiveLabel(
                modifier = Modifier.constrainAs(selectedObjective) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
            )
        }
    }

    @Composable
    private fun ViewerViewModel.GridData(modifier: Modifier) {
        // TODO lazy grid
        Box(
            modifier = modifier
                .fillMaxSize()
                .horizontalScroll(rememberSaveable(saver = ScrollState.Saver) { horizontalScroll })
                .verticalScroll(rememberSaveable(saver = ScrollState.Saver) { verticalScroll })
        ) {
            MapGrid()

            if (grid.rows.isNotEmpty()) {
                objectiveIcons.forEach { objective -> Objective(objective) }
                bloodlusts.forEach { bloodlust -> bloodlust.Bloodlust() }
            }
        }

        scrollToRegion()
    }

    /**
     * Lays out the map represented by a tiled grid.
     */
    @Composable
    private fun ViewerViewModel.MapGrid() = Column(
        modifier = Modifier.fillMaxSize()
    ) {
        grid.rows.forEach { row ->
            Row {
                row.forEach { tile -> tile.MapTile() }
            }
        }
    }

    /**
     * Lays out an individual tile within the grid.
     */
    @Composable
    private fun Tile.MapTile() {
        val hasContent = content.isNotEmpty()

        // Need to specify non-zero width/height on the default bitmap.
        val bitmap = if (hasContent) content.asImageBitmap() else ImageBitmap(1, 1)
        Image(
            painter = BitmapPainter(bitmap),
            contentDescription = AppResources.strings.wvw_tile.localized(),
            modifier = Modifier
                .size(width.toDp(), height.toDp())
                .clickable(
                    // Disable the ripple so that the illusion of a contiguous map is not broken.
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // Clear the objective pop-up.
                    model.selected.value = null
                }
        )

        LaunchedEffect(hasContent) {
            // Only refresh individually if the whole grid doesn't need
            if (!model.refreshGrid && !hasContent) {
                model.request(this@MapTile)
            }
        }
    }

    /**
     * Lays out the image indicating the owner of bloodlust.
     */
    @Composable
    private fun Bloodlust.Bloodlust() {
        val width = 64
        val height = 64

        // Displace the coordinates so that it aligns with the center of the image.
        val displacedX = x - width / 2
        val displacedY = y - height / 2

        AsyncImage(
            image = link,
            width = width,
            height = height,
            color = color,
            description = description
        ).Content(
            modifier = Modifier.absoluteOffset(
                x = displacedX.toDp(),
                y = displacedY.toDp()
            ),
        )
    }

    /**
     * Lays out the individual objective on the map.
     */
    @Composable
    private fun ViewerViewModel.Objective(objective: ObjectiveIcon) {
        val width = 64
        val height = 64

        // Displace the coordinates so that it aligns with the center of the image.
        val displacedX = objective.x - width / 2
        val displacedY = objective.y - height / 2

        ConstraintLayout(
            modifier = Modifier
                .absoluteOffset(displacedX.toDp(), displacedY.toDp())
                .wrapContentSize()
        ) {
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

    /**
     * Lays out the indicators for the objective's guild if it has been claimed.
     */
    @Composable
    private fun ObjectiveClaim.Claim(modifier: Modifier) = AsyncImage(
        enabled = enabled,
        image = link,
        description = description,
        width = 32,
        height = 32,
    ).Content(modifier = modifier)

    /**
     * Lays out the indicators for the objective's upgrade progression level.
     */
    @Composable
    private fun ObjectiveProgression.Progression(modifier: Modifier) = AsyncImage(
        enabled = enabled,
        image = link,
        description = description,
        width = 32,
        height = 32,
        color = color
    ).Content(modifier = modifier)

    /**
     * Lays out the indicator for the objective's permanent or temporary waypoint if it exists.
     */
    @Composable
    private fun ObjectiveWaypoint.Waypoint(modifier: Modifier) = AsyncImage(
        enabled = enabled,
        image = link,
        description = description,
        width = 32,
        height = 32,
        color = color
    ).Content(modifier = modifier)

    /**
     * Lays out the image of the objective.
     */
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ObjectiveIcon.Image(modifier: Modifier, width: Int, height: Int) {
        val mapRouter = LocalMapRouter.current
        AsyncImage(
            width = width,
            height = height,
            image = link,
            description = description,
            color = color
        ).Content(
            modifier = modifier.combinedClickable(
                onLongClick = {
                    // Swap pages to display all of the information instead of the limited information that normally comes with the pop-up.
                    val config = MapConfig.ObjectiveConfig(objective.id.value)
                    mapRouter.bringToFront(config)
                },
                onClick = {
                    // Set the selected objective for displaying the pop-up.
                    model.selected.value = objective
                }
            ),
        )
    }

    /**
     * Lays out the timer for the amount of time an objective is immune from capture.
     */
    @Composable
    private fun ObjectiveImmunity.ImmunityTimer(modifier: Modifier) {
        // If the time has finished or the current time is incorrectly set and thus causing an inflated remaining time, do not display it.
        // For the latter case, while the timers shown will be incorrect they will at the very least not be inflated.
        val remaining = remaining.collectAsState(initial = Duration.ZERO).value
        if (!remaining.isPositive() || duration == null || remaining > duration) return

        Text(
            text = remaining.minuteFormat(),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.White,
            modifier = modifier.wrapContentSize()
        )
    }

    /**
     * Lays out general information about the objective the user clicked on in a pop-up label.
     */
    @Composable
    private fun ViewerViewModel.SelectedObjectiveLabel(modifier: Modifier) {
        val selected = selectedObjective ?: return
        BackgroundImage(
            modifier = modifier,
            painter = relativeBackgroundPainter,
            presenter = relativeBackgroundPresenter
        ) {
            Column(modifier = Modifier.padding(horizontal = 5.dp)) {
                val textSize = 16.sp
                Text(text = selected.title.localized(), fontSize = textSize, fontWeight = FontWeight.Bold)
                selected.subtitle?.let { subtitle ->
                    Text(text = subtitle.localized(), fontSize = textSize)
                }
            }
        }
    }
}