package com.bselzer.gw2.manager.android.ui.activity.wvw.page

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.constraintlayout.compose.ConstraintLayout
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.Transformation
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.ui.activity.wvw.WvwPage
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.common.ImageState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.BloodlustState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.SelectedObjectiveState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.WvwMapState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.grid.TileState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.objective.ImmunityState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.objective.IndicatorState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.objective.ObjectiveState
import com.bselzer.gw2.manager.android.ui.coil.ColorTransformation
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.ktx.compose.ui.unit.toDp
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime

class WvwMapPage(
    theme: Theme,
    imageLoader: ImageLoader,
    appBarActions: @Composable RowScope.() -> Unit,
    state: WvwMapState,
    private val setPage: (WvwPage.PageType) -> Unit,
) : WvwContentPage<WvwMapState>(theme, imageLoader, appBarActions, state) {
    @Composable
    override fun Content() {
        Column {
            TopAppBar()

            val pinchToZoom = rememberTransformableState { zoomChange, _, _ ->
                // Allow the user to change the zoom by pinching the map.
                val change = if (zoomChange > 1) 1 else -1
                state.changeZoom(change)
            }

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .transformable(pinchToZoom)
            ) {
                val (map, selectedObjective) = createRefs()
                GridData(Modifier.constrainAs(map) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })

                // Overlay the selected objective over everything else on the map.
                state.mapSelectedObjective.value?.let {
                    SelectedObjectiveLabel(
                        selected = it,
                        modifier = Modifier.constrainAs(selectedObjective) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                    )
                }

                // TODO show map names (borderlands preferably with team names)
            }
        }

        // Display a progress bar until tiling is finished.
        if (state.shouldShowMissingGridData.value) {
            MissingGridData()
        }
    }

    @Composable
    override fun topAppBarTitle(): String = stringResource(id = R.string.wvw_map)

    @Composable
    override fun TopAppBarActions() = Box {
        var isExpanded by remember { mutableStateOf(false) }
        IconButton(onClick = { isExpanded = true }) {
            Icon(Icons.Filled.MoreVert, contentDescription = "More Options")
        }

        DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            // Only enable zoom in/zoom out buttons when within the range.
            val zoom = state.currentZoom()
            val range = state.zoomRange()

            IconButton(enabled = zoom < range.last, onClick = {
                state.changeZoom(increment = 1)
                isExpanded = false
            }) {
                Icon(painter = painterResource(id = R.drawable.ic_zoom_in), contentDescription = "Zoom In")
            }

            IconButton(enabled = zoom > range.first, onClick = {
                state.changeZoom(increment = -1)
                isExpanded = false
            }) {
                Icon(painter = painterResource(id = R.drawable.ic_zoom_out), contentDescription = "Zoom Out")
            }
        }
    }

    /**
     * Lays out the content related to the grid data not being populated.
     */
    @Composable
    private fun MissingGridData() = Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ProgressIndicator()
    }

    /**
     * Lays out the grid content.
     */
    @Composable
    private fun GridData(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .horizontalScroll(state.horizontalScroll)
                .verticalScroll(state.verticalScroll)
        ) {
            MapGrid()
            Objectives()

            if (state.shouldShowBloodlust.value) {
                state.bloodlusts.value.forEach {
                    Bloodlust(bloodlust = it)
                }
            }
        }

        ScrollToRegion()
    }

    /**
     * Scrolls the map to the configured WvW map within the grid.
     */
    @Composable
    private fun ScrollToRegion() {
        var shouldScroll by state.shouldScrollToRegion
        LaunchedEffect(key1 = shouldScroll) {
            if (shouldScroll) {
                val coordinates by state.scrollToRegionCoordinates
                state.horizontalScroll.animateScrollTo(coordinates.first)
                state.verticalScroll.animateScrollTo(coordinates.second)
                shouldScroll = false
            }
        }
    }

    /**
     * Lays out the map represented by a tiled grid.
     */
    @Composable
    private fun MapGrid() = Column(
        modifier = Modifier.fillMaxSize()
    ) {
        for (row in state.mapGrid.value.tiles) {
            Row {
                for (tile in row) {
                    MapTile(tile)
                }
            }
        }
    }

    /**
     * Lays out an individual tile within the grid.
     */
    @Composable
    private fun MapTile(tile: TileState) {
        // Need to specify non-zero width/height on the default bitmap.
        val bitmap = if (tile.content.isEmpty()) Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) else BitmapFactory.decodeByteArray(tile.content, 0, tile.content.size)
        Image(
            painter = BitmapPainter(bitmap.asImageBitmap()),
            contentDescription = "World vs. World Map",
            modifier = Modifier
                .size(tile.width.toDp(), tile.height.toDp())
                .clickable(
                    // Disable the ripple so that the illusion of a contiguous map is not broken.
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // Clear the objective pop-up.
                    state.select(objective = null)
                }
        )
    }

    /**
     * Lays out the objectives on the map.
     */
    @Composable
    private fun Objectives() {
        // Render from bottom right to top left so that overlap is consistent.
        val comparator = compareByDescending<ObjectiveState> { objective -> objective.y }.thenByDescending { objective -> objective.x }
        state.mapGrid.value.objectives.sortedWith(comparator).forEach { objective ->
            Objective(objective)
        }
    }

    /**
     * Lays out the individual objective on the map.
     */
    @OptIn(ExperimentalTime::class)
    @Composable
    private fun Objective(objective: ObjectiveState) = ConstraintLayout(
        modifier = Modifier
            .absoluteOffset(objective.x.toDp(), objective.y.toDp())
            .wrapContentSize()
    ) {
        val (icon, timer, upgradeIndicator, claimIndicator, waypointIndicator) = createRefs()

        // Overlay the objective image onto the map image.
        ObjectiveImage(
            image = objective.image,
            objective = objective.objective,
            modifier = Modifier.constrainAs(icon) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )

        Indicator(
            indicator = objective.progression,
            modifier = Modifier.constrainAs(upgradeIndicator) {
                // Display the indicator in the top center of the objective icon.
                top.linkTo(icon.top)
                start.linkTo(icon.start)
                end.linkTo(icon.end)
            },
        )

        Indicator(
            indicator = objective.claim,
            modifier = Modifier.constrainAs(claimIndicator) {
                // Display the indicator in the bottom right of the objective icon.
                bottom.linkTo(icon.bottom)
                end.linkTo(icon.end)
            }
        )

        val waypointColor = objective.waypoint.color?.toArgb()
        val waypointTransformations: Array<Transformation> = if (waypointColor == null) emptyArray() else arrayOf(ColorTransformation(waypointColor))
        Indicator(
            indicator = objective.waypoint,
            transformations = waypointTransformations,
            modifier = Modifier.constrainAs(waypointIndicator) {
                // Display the indicator in the bottom left of the objective icon.
                bottom.linkTo(icon.bottom)
                start.linkTo(icon.start)
            },
        )

        ImmunityTimer(
            immunity = objective.immunity,
            modifier = Modifier.constrainAs(timer) {
                // Display the timer underneath the objective icon.
                top.linkTo(icon.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }

    /**
     * Lays out the image of the objective.
     */
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ObjectiveImage(modifier: Modifier, image: ImageState, objective: WvwObjective) {
        val request = ImageRequest.Builder(LocalContext.current)
            .data(image.link)
            .size(width = image.width, height = image.height)
            .transformations(ColorTransformation(image.color.toArgb()))
            .build()

        Image(
            painter = rememberImagePainter(request = request, imageLoader = imageLoader),
            contentDescription = image.description,
            contentScale = ContentScale.Fit,
            modifier = modifier
                .size(width = image.width.toDp(), height = image.height.toDp())
                .combinedClickable(onLongClick = {
                    // Swap pages to display all of the information instead of the limited information that normally comes with the pop-up.
                    state.select(objective)
                    setPage(WvwPage.PageType.DETAILED_SELECTED_OBJECTIVE)
                }) {
                    state.select(objective)
                }
        )
    }

    /**
     * Lays out an indicator icon.
     */
    @Composable
    private fun Indicator(
        modifier: Modifier,
        indicator: IndicatorState,
        vararg transformations: Transformation
    ) {
        if (!indicator.enabled || indicator.link.isNullOrBlank()) return

        val request = ImageRequest.Builder(LocalContext.current)
            .data(indicator.link)
            .size(width = indicator.width, height = indicator.height)
            .transformations(*transformations)
            .build()

        Image(
            painter = rememberImagePainter(request, imageLoader),
            contentDescription = indicator.description,
            contentScale = ContentScale.Fit,
            modifier = modifier.size(width = indicator.width.toDp(), height = indicator.height.toDp())
        )
    }

    /**
     * Lays out the timer for the amount of time an objective is immune from capture.
     */
    @OptIn(ExperimentalTime::class)
    @Composable
    private fun ImmunityTimer(modifier: Modifier, immunity: ImmunityState) {
        // If the time has finished or the current time is incorrectly set and thus causing an inflated remaining time, do not display it.
        // For the latter case, while the timers shown will be incorrect they will at the very least not be inflated.
        if (!immunity.enabled || !immunity.remaining.isPositive() || immunity.duration == null || immunity.remaining > immunity.duration) return

        Text(
            text = immunity.formattedRemaining,
            fontWeight = FontWeight.Bold,
            fontSize = immunity.textSize,
            color = Color.White,
            modifier = modifier.wrapContentSize()
        )

        // Recompose every immunity.delay for an updated timer.
        var temp by remember { mutableStateOf(Int.MIN_VALUE) }
        LaunchedEffect(key1 = temp) {
            delay(immunity.delay)
            temp += 1
        }
    }

    /**
     * Lays out general information about the objective the user clicked on in a pop-up label.
     */
    @Composable
    private fun SelectedObjectiveLabel(modifier: Modifier, selected: SelectedObjectiveState) = RelativeBackgroundColumn(modifier = modifier) {
        val textSize = selected.textSize
        Text(text = selected.title, fontSize = textSize, fontWeight = FontWeight.Bold)
        selected.subtitle?.let { subtitle ->
            Text(text = subtitle, fontSize = textSize)
        }
    }

    /**
     * Lays out the bloodlust icon.
     */
    @Composable
    private fun Bloodlust(bloodlust: BloodlustState) {
        val request = ImageRequest.Builder(LocalContext.current)
            .data(bloodlust.link)
            .size(width = bloodlust.width, height = bloodlust.height)
            .transformations(ColorTransformation(color = bloodlust.color.toArgb()))
            .build()

        Image(
            painter = rememberImagePainter(request, imageLoader),
            contentDescription = bloodlust.description,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .absoluteOffset(x = bloodlust.x.toDp(), y = bloodlust.y.toDp())
                .size(width = bloodlust.width.toDp(), height = bloodlust.height.toDp())
        )
    }
}