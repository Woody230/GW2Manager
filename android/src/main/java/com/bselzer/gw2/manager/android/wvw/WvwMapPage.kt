package com.bselzer.gw2.manager.android.wvw

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.constraintlayout.compose.ConstraintLayout
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.gw2.manager.common.state.AppState
import com.bselzer.gw2.manager.common.state.map.SelectedObjectiveState
import com.bselzer.gw2.manager.common.state.map.WvwMapState
import com.bselzer.gw2.manager.common.state.map.grid.TileState
import com.bselzer.gw2.manager.common.state.map.objective.ImmunityState
import com.bselzer.gw2.manager.common.state.map.objective.ObjectiveState
import com.bselzer.gw2.manager.common.state.selected.WvwSelectedState
import com.bselzer.gw2.manager.common.ui.composable.ImageContent
import com.bselzer.gw2.manager.common.ui.composable.ImageState
import com.bselzer.gw2.v2.cache.instance.ContinentCache
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.tile.model.response.Tile
import com.bselzer.gw2.v2.tile.model.response.TileGrid
import com.bselzer.ktx.compose.ui.unit.toDp
import com.bselzer.ktx.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.ExperimentalTime

class WvwMapPage(
    aware: Gw2Aware,
    navigationIcon: @Composable () -> Unit,
    state: WvwMapState,
) : WvwPage<WvwMapState>(aware, navigationIcon, state) {
    private val page = mutableStateOf(MapPageType.MAP)

    private enum class MapPageType {
        MAP,
        SELECTED
    }

    @Composable
    override fun background(): BackgroundType = when (remember { page }.value) {
        MapPageType.MAP -> BackgroundType.NONE
        MapPageType.SELECTED -> BackgroundType.ABSOLUTE
    }

    @Composable
    override fun CoreContent() {
        var page by page
        when (page) {
            MapPageType.MAP -> {
                Map()
            }
            MapPageType.SELECTED -> {
                val selectedState = remember { WvwSelectedState(aware = this, selectedObjective = state.selectedObjective) }
                WvwSelectedObjectivePage(aware = this, state = selectedState).Content()
            }
        }

        // Tile refresh is comparatively expensive to typical fetching so only do it when the user is on this page.
        val appPage by appState.page
        LaunchedEffect(page, appPage, state.currentZoom()) {
            refresh()
        }

        // If we are not on the core subpage of this page, then reset to the core subpage.
        // Otherwise, let the caller manage back state.
        BackHandler(enabled = appPage == AppState.Companion.PageType.WVW_MAP && page != MapPageType.MAP) {
            // Clear the objective pop-up when returning.
            if (page == MapPageType.SELECTED) {
                state.selectedObjective.value = null
            }

            page = MapPageType.MAP
        }
    }

    @Composable
    override fun title(): String = stringResource(id = R.string.wvw_map)

    override suspend fun refresh() {
        refreshMapData()
        refreshGridData()
    }

    @Composable
    override fun dropdownIcons(): (@Composable ((Boolean) -> Unit) -> Unit)? = when (remember { page }.value) {
        MapPageType.MAP -> mapDropdownIcons()
        MapPageType.SELECTED -> null
    }

    /**
     * Lays out the icons for zooming in/out the map.
     */
    @Composable
    private fun mapDropdownIcons(): (@Composable ((Boolean) -> Unit) -> Unit) = { setExpanded ->
        // Only enable zoom in/zoom out buttons when within the range.
        val zoom = remember { state.currentZoom() }
        val range = remember { state.zoomRange() }

        IconButton(enabled = zoom < range.last, onClick = {
            state.changeZoom(increment = 1)
            setExpanded(false)
        }) {
            Icon(painter = painterResource(id = R.drawable.ic_zoom_in_map), contentDescription = "Zoom In")
        }

        IconButton(enabled = zoom > range.first, onClick = {
            state.changeZoom(increment = -1)
            setExpanded(false)
        }) {
            Icon(painter = painterResource(id = R.drawable.ic_zoom_out_map), contentDescription = "Zoom Out")
        }
    }

    @Composable
    private fun Map() = Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
            remember { state.mapSelectedObjective }.value?.let {
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

        // Display a progress bar until tiling is finished.
        if (remember { state.shouldShowMissingGridData }.value) {
            MissingGridData()
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
                .horizontalScroll(rememberSaveable(saver = ScrollState.Saver) { state.horizontalScroll })
                .verticalScroll(rememberSaveable(saver = ScrollState.Saver) { state.verticalScroll })
        ) {
            MapGrid()

            if (remember { state.shouldShowObjectives }.value) {
                Objectives()
            }

            if (remember { state.shouldShowBloodlust }.value) {
                remember { state.bloodlusts }.value.forEach { bloodlust ->
                    bloodlust.ImageContent(modifier = Modifier.absoluteOffset(x = bloodlust.x.toDp(), y = bloodlust.y.toDp()))
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
        var shouldScroll by remember { state.shouldScrollToRegion }
        val coordinates by remember { state.scrollToRegionCoordinates }
        LaunchedEffect(shouldScroll) {
            if (shouldScroll) {
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
        for (row in remember { state.mapGrid }.value.tiles) {
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
                    state.selectedObjective.value = null
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
        remember { state.mapGrid }.value.objectives.sortedWith(comparator).forEach { objective ->
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

        objective.progression.ImageContent(
            modifier = Modifier.constrainAs(upgradeIndicator) {
                // Display the indicator in the top center of the objective icon.
                top.linkTo(icon.top)
                start.linkTo(icon.start)
                end.linkTo(icon.end)
            },
        )

        objective.claim.ImageContent(
            modifier = Modifier.constrainAs(claimIndicator) {
                // Display the indicator in the bottom right of the objective icon.
                bottom.linkTo(icon.bottom)
                end.linkTo(icon.end)
            }
        )

        objective.waypoint.ImageContent(
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
    private fun ObjectiveImage(modifier: Modifier, image: ImageState, objective: WvwObjective) = image.ImageContent(
        modifier = modifier.combinedClickable(onLongClick = {
            // Swap pages to display all of the information instead of the limited information that normally comes with the pop-up.
            state.selectedObjective.value = objective
            page.value = MapPageType.SELECTED
        }) {
            state.selectedObjective.value = objective
        },
    )

    /**
     * Lays out the timer for the amount of time an objective is immune from capture.
     */
    @OptIn(ExperimentalTime::class)
    @Composable
    private fun ImmunityTimer(modifier: Modifier, immunity: ImmunityState) {
        // If the time has finished or the current time is incorrectly set and thus causing an inflated remaining time, do not display it.
        // For the latter case, while the timers shown will be incorrect they will at the very least not be inflated.
        val duration = immunity.duration
        if (!immunity.enabled || !immunity.remaining.isPositive() || duration == null || immunity.remaining > duration) return

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

    // region Refresh

    /**
     * Refreshes the WvW map tiling grid.
     */
    private suspend fun refreshGridData() = withContext(Dispatchers.IO) {
        gw2Cache.instance {
            val continent = state.continent.value
            val floor = state.floor.value

            // Verify that the related data exists.
            if (continent == null || floor == null) {
                return@instance
            }

            val zoom = state.currentZoom()
            Logger.d("Refreshing WvW tile grid data for zoom level $zoom.")

            val gridRequest = tileClient.requestGrid(continent, floor, zoom).let { request ->
                if (configuration.wvw.map.isBounded) {
                    // Cut off unneeded tiles.
                    val bound = configuration.wvw.map.levels.firstOrNull { level -> level.zoom == zoom }?.bound
                    if (bound != null) {
                        return@let request.bounded(startX = bound.startX, startY = bound.startY, endX = bound.endX, endY = bound.endY)
                    } else {
                        Logger.w("Unable to create a bounded request for zoom level $zoom.")
                    }
                }

                return@let request
            }

            // Set up the grid without content in the tiles.
            state.grid.value = TileGrid(gridRequest, gridRequest.tileRequests.map { tileRequest -> Tile(tileRequest) })

            // Defer the content for parallelism and populate it when its ready.
            for (deferred in tileCache.findTilesAsync(gridRequest.tileRequests)) {
                val tile = deferred.await()
                state.tileContent[tile] = tile.content
            }
        }
    }

    /**
     * Refreshes the WvW map data using the configuration ids.
     */
    private suspend fun refreshMapData() = withContext(Dispatchers.IO) {
        gw2Cache.instance {
            Logger.d("Refreshing WvW map data.")

            // Assume that all WvW maps are within the same continent and floor.
            val mapId = appState.match.value?.maps?.firstOrNull()?.id
            if (mapId == null) {
                // Default to what is in the config to determine the correct continent.
                val cache = get<ContinentCache>()
                val continent = cache.getContinent(configuration.wvw.map.continentId)
                state.floor.value = cache.getContinentFloor(configuration.wvw.map.continentId, configuration.wvw.map.floorId)
                state.continent.value = continent
            } else {
                // Get the associated continent from the map.
                val cache = get<ContinentCache>()
                val map = cache.getMap(mapId)
                val continent = cache.getContinent(map)
                state.floor.value = cache.getContinentFloor(map)
                state.continent.value = continent
            }
        }
    }

    // endregion Refresh
}