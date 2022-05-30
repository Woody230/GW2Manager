package com.bselzer.gw2.manager.common.ui.layout.main.content.map

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.arkivanov.decompose.router.bringToFront
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.image.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.image.Content
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MapConfig
import com.bselzer.gw2.manager.common.ui.layout.main.model.map.*
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
        Box(
            modifier = modifier
                .fillMaxSize()
                .horizontalScroll(rememberSaveable(saver = ScrollState.Saver) { horizontalScroll })
                .verticalScroll(rememberSaveable(saver = ScrollState.Saver) { verticalScroll })
        ) {
            MapGrid()
            Objectives()
            bloodlusts.forEach { bloodlust -> bloodlust.Bloodlust() }
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
        // Need to specify non-zero width/height on the default bitmap.
        val bitmap = if (content.isEmpty()) ImageBitmap(1, 1) else content.asImageBitmap()
        Image(
            painter = BitmapPainter(bitmap),

            // TODO translate
            contentDescription = "World vs. World Map",
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
    }

    /**
     * Lays out the image indicating the owner of bloodlust.
     */
    @Composable
    private fun Bloodlust.Bloodlust() = AsyncImage(
        enabled = enabled,
        image = link,
        width = width,
        height = height,
        color = color,
        description = description
    ).Content(
        modifier = Modifier.absoluteOffset(x = x.toDp(), y = y.toDp()),
    )

    /**
     * Lays out the objectives on the map.
     */
    @Composable
    private fun ViewerViewModel.Objectives() {
        // Render from bottom right to top left so that overlap is consistent.
        val comparator = compareByDescending<ObjectiveIcon> { objective -> objective.y }.thenByDescending { objective -> objective.x }
        objectiveIcons.sortedWith(comparator).forEach { objective ->
            Objective(objective)
        }
    }

    /**
     * Lays out the individual objective on the map.
     */
    @Composable
    private fun ViewerViewModel.Objective(objective: ObjectiveIcon) = ConstraintLayout(
        modifier = Modifier
            .absoluteOffset(objective.x.toDp(), objective.y.toDp())
            .wrapContentSize()
    ) {
        val (icon, timer, upgradeIndicator, claimIndicator, waypointIndicator) = createRefs()

        // Overlay the objective image onto the map image.
        objective.Image(
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

    /**
     * Lays out the indicators for the objective's guild if it has been claimed.
     */
    @Composable
    private fun ObjectiveClaim.Claim(modifier: Modifier) = AsyncImage(
        enabled = enabled,
        image = link,
        description = description,
        width = width,
        height = height,
    ).Content(modifier = modifier)

    /**
     * Lays out the indicators for the objective's upgrade progression level.
     */
    @Composable
    private fun ObjectiveProgression.Progression(modifier: Modifier) = AsyncImage(
        enabled = enabled,
        image = link,
        description = description,
        width = width,
        height = height,
        color = color
    ).Content(
        modifier = modifier,
    )

    /**
     * Lays out the indicator for the objective's permanent or temporary waypoint if it exists.
     */
    @Composable
    private fun ObjectiveWaypoint.Waypoint(modifier: Modifier) = AsyncImage(
        enabled = enabled,
        image = link,
        description = description,
        width = width,
        height = height,
        color = color
    ).Content(
        modifier = modifier,
    )

    /**
     * Lays out the image of the objective.
     */
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ObjectiveIcon.Image(modifier: Modifier) {
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
                    val config = MapConfig.ObjectiveConfig(objective.id)
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
        if (!enabled || !remaining.isPositive() || duration == null || remaining > duration) return

        Text(
            text = remaining.minuteFormat(),
            fontWeight = FontWeight.Bold,

            // TODO remove from config
            fontSize = model.configuration.wvw.objectives.immunity.textSize.sp,
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
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TODO remove from config
                val textSize = configuration.wvw.objectives.selected.textSize.sp
                Text(text = selected.title.localized(), fontSize = textSize, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                selected.subtitle?.let { subtitle ->
                    Text(text = subtitle.localized(), fontSize = textSize, textAlign = TextAlign.Center)
                }
            }
        }
    }
}