package com.bselzer.gw2.manager.ui.activity.wvw

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.Transformation
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.companion.preference.WvwPreferenceCompanion.REFRESH_INTERVAL
import com.bselzer.gw2.manager.companion.preference.WvwPreferenceCompanion.SELECTED_WORLD
import com.bselzer.gw2.manager.configuration.wvw.WvwUpgradeProgression
import com.bselzer.gw2.manager.ui.activity.common.BaseActivity
import com.bselzer.gw2.manager.ui.activity.main.MainActivity
import com.bselzer.gw2.manager.ui.activity.wvw.WvwActivity.Page.*
import com.bselzer.gw2.manager.ui.coil.HexColorTransformation
import com.bselzer.gw2.manager.ui.theme.AppTheme
import com.bselzer.library.gw2.v2.cache.instance.ContinentCache
import com.bselzer.library.gw2.v2.cache.instance.WorldCache
import com.bselzer.library.gw2.v2.cache.instance.WvwCache
import com.bselzer.library.gw2.v2.model.continent.Continent
import com.bselzer.library.gw2.v2.model.continent.ContinentFloor
import com.bselzer.library.gw2.v2.model.enumeration.extension.wvw.owner
import com.bselzer.library.gw2.v2.model.enumeration.extension.wvw.type
import com.bselzer.library.gw2.v2.model.enumeration.wvw.MapBonusType
import com.bselzer.library.gw2.v2.model.enumeration.wvw.MapType
import com.bselzer.library.gw2.v2.model.enumeration.wvw.ObjectiveOwner
import com.bselzer.library.gw2.v2.model.enumeration.wvw.ObjectiveType
import com.bselzer.library.gw2.v2.model.extension.continent.continentRectangle
import com.bselzer.library.gw2.v2.model.extension.wvw.coordinates
import com.bselzer.library.gw2.v2.model.extension.wvw.objective
import com.bselzer.library.gw2.v2.model.extension.wvw.position
import com.bselzer.library.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.library.gw2.v2.model.world.World
import com.bselzer.library.gw2.v2.model.wvw.match.WvwMapObjective
import com.bselzer.library.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.library.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.library.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.library.gw2.v2.tile.model.response.Tile
import com.bselzer.library.gw2.v2.tile.model.response.TileGrid
import com.bselzer.library.kotlin.extension.coroutine.cancel
import com.bselzer.library.kotlin.extension.coroutine.repeat
import com.bselzer.library.kotlin.extension.function.collection.addTo
import com.bselzer.library.kotlin.extension.function.collection.isOneOf
import com.bselzer.library.kotlin.extension.function.objects.userFriendly
import com.bselzer.library.kotlin.extension.geometry.dimension.bi.Dimension2D
import com.bselzer.library.kotlin.extension.geometry.dimension.bi.position.Point2D
import com.bselzer.library.kotlin.extension.preference.nullLatest
import com.bselzer.library.kotlin.extension.preference.safeLatest
import com.bselzer.library.kotlin.extension.preference.update
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*
import timber.log.Timber
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class WvwActivity : BaseActivity() {
    private val jobs: ArrayDeque<Job> = ArrayDeque()
    private val worlds = mutableStateOf<Collection<World>>(emptyList())
    private val match = mutableStateOf<WvwMatch?>(null)
    private val objectives = mutableStateOf<Collection<WvwObjective>>(emptyList())
    private val upgrades = mutableStateOf(emptyMap<Int, WvwUpgrade>())
    private val guildUpgrades = mutableStateOf(emptyMap<Int, GuildUpgrade>())
    private val continent = mutableStateOf<Continent?>(null)
    private val floor = mutableStateOf<ContinentFloor?>(null)
    private val grid = mutableStateOf(TileGrid())
    private val selectedObjective = mutableStateOf<WvwObjective?>(null)
    private val tileContent = mutableStateMapOf<Tile, Bitmap>()
    private val zoom = MutableStateFlow(0)
    private val selectedPage = mutableStateOf<Page?>(null)

    private enum class Page {
        MAP,
        MATCH,
        DETAILED_SELECTED_OBJECTIVE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        zoom.value = configuration.wvw.map.zoom.default
        setContent { Content() }
    }

    @OptIn(ExperimentalTime::class)
    override fun onResume() {
        super.onResume()

        CoroutineScope(Dispatchers.IO).launch {
            val interval = datastore.safeLatest(REFRESH_INTERVAL, 5)
            repeat(Duration.minutes(interval)) {
                refreshData()
            }
        }.addTo(jobs)

        CoroutineScope(Dispatchers.IO).launch {
            // Zoom has changed so notify that the grid needs to be refreshed.
            zoom.shareIn(this, SharingStarted.Lazily).onEach {
                gw2Cache.lockedInstance {
                    refreshGridData()
                }
            }.collect()
        }.addTo(jobs)
    }

    override fun onPause() {
        super.onPause()
        jobs.cancel()
    }

    // region Refresh
    /**
     * Refreshes the WvW data.
     */
    private suspend fun refreshData() {
        Timber.d("Refreshing WvW data.")

        val selectedWorld = datastore.nullLatest(SELECTED_WORLD)
        gw2Cache.lockedInstance {
            worlds.value = get<WorldCache>().findWorlds()

            // Need the world to be able to get the associated match.
            if (selectedWorld == null) {
                // Selection is required so do not allow cancellation.
                showSelectWorldDialog(cancellable = false)

                // Use the config ids to try to populate the map/grid data before the selection is made.
                refreshMapData()
                refreshGridData()
                return@lockedInstance
            }

            val cache = get<WvwCache>()
            val match = gw2Client.wvw.match(selectedWorld)
            cache.putMatch(match)
            val objectives = cache.findObjectives(match)

            this@WvwActivity.match.value = match
            this@WvwActivity.objectives.value = objectives
            this@WvwActivity.upgrades.value = cache.findUpgrades(objectives).associateBy { it.id }
            refreshMapData(match)
            refreshGridData()
        }
    }

    /**
     * Refreshes the WvW map data using the configuration ids.
     */
    private suspend fun refreshMapData() = gw2Cache.instance {
        // This data should not be changing so only initialize it.
        if (continent.value != null) {
            return@instance
        }

        Timber.d("Refreshing WvW map data.")

        // Assume that all WvW maps are within the same continent and floor.
        val cache = get<ContinentCache>()
        val continent = cache.getContinent(configuration.wvw.map.continentId)
        this@WvwActivity.floor.value = cache.getContinentFloor(configuration.wvw.map.continentId, configuration.wvw.map.floorId)
        this@WvwActivity.continent.value = continent
    }

    /**
     * Refreshes the WvW map data using a map found from the match.
     */
    private suspend fun refreshMapData(match: WvwMatch) = gw2Cache.instance {
        // This data should not be changing so only initialize it.
        if (continent.value != null) {
            return@instance
        }

        Timber.d("Refreshing WvW map data.")

        // Assume that all WvW maps are within the same continent and floor.
        val mapId = match.maps.firstOrNull()?.id ?: return@instance
        val cache = get<ContinentCache>()
        val map = cache.getMap(mapId)
        val continent = cache.getContinent(map)
        this@WvwActivity.floor.value = cache.getContinentFloor(map)
        this@WvwActivity.continent.value = continent
    }

    /**
     * Refreshes the WvW map tiling grid.
     */
    private suspend fun refreshGridData() = gw2Cache.instance {
        val continent = continent.value
        val floor = floor.value

        // Verify that the related data exists.
        if (continent == null || floor == null) {
            return@instance
        }

        val zoom = zoom.value
        Timber.d("Refreshing WvW tile grid data for zoom level $zoom.")

        val gridRequest = tileClient.requestGrid(continent, floor, zoom).let { request ->
            if (configuration.wvw.map.isBounded) {
                // Cut off unneeded tiles.
                val bound = configuration.wvw.map.levels.firstOrNull { level -> level.zoom == zoom }?.bound
                if (bound != null) {
                    return@let request.bounded(startX = bound.startX, startY = bound.startY, endX = bound.endX, endY = bound.endY)
                } else {
                    Timber.w("Unable to create a bounded request for zoom level $zoom.")
                }
            }

            return@let request
        }

        // Set up the grid without content in the tiles.
        grid.value = TileGrid(gridRequest, gridRequest.tileRequests.map { tileRequest -> Tile(tileRequest) })

        // Defer the content for parallelism and populate it when its ready.
        for (deferred in tileCache.findTilesAsync(gridRequest.tileRequests)) {
            val tile = deferred.await()
            val bitmap = BitmapFactory.decodeByteArray(tile.content, 0, tile.content.size)
            tileContent[tile] = bitmap
        }
    }
    // endregion Refresh

    // region ShowMenu

    @Composable
    private fun Content() = AppTheme {
        when (remember { selectedPage }.value) {
            MAP -> ShowMapPage()
            MATCH -> ShowMatchPage()
            DETAILED_SELECTED_OBJECTIVE -> ShowDetailedSelectedObjectivePage()
            null -> ShowMenu()
        }
    }

    /**
     * Displays the World vs. World menu.
     */
    @Composable
    private fun ShowMenu() = Column {
        // Provide the illusion that we haven't swapped screens from the MainActivity.
        ShowMenuAppBar()
        Box {
            ShowBackground(drawableId = R.drawable.gw2_two_sylvari)

            ShowMenu(background = R.drawable.gw2_ice, "Map" to { selectedPage.value = MAP }, "Match" to { selectedPage.value = MATCH })
        }
    }

    /**
     * Displays the app bar for when there is no [selectedPage].
     */
    @Composable
    private fun ShowMenuAppBar() = TopAppBar(
        title = { Text(text = stringResource(id = R.string.activity_wvw)) },
        navigationIcon = {
            // Disable the animation to give the illusion that we haven't swapped screens.
            val intent = Intent(this@WvwActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            UpNavigationIcon(intent)
        },
    )

    // endregion ShowMenu

    // region ShowMap

    @Composable
    private fun ShowMapPage() {
        val grid = remember { grid }.value
        val tileContent = remember { tileContent }
        val zoom by zoom.collectAsState()

        // Display the background until tiling occurs.
        val contentSize = tileContent.filterKeys { key -> key.zoom == zoom }.size
        if (grid.tiles.isEmpty() || contentSize == 0) {
            ShowBackground(drawableId = R.drawable.gw2_ice)
        }

        Column {
            ShowMapAppBar()

            val pinchToZoom = rememberTransformableState { zoomChange, _, _ ->
                // Allow the user to change the zoom by pinching the map.
                val change = if (zoomChange > 1) 1 else -1
                changeZoom(change)
            }

            ConstraintLayout(
                modifier = Modifier.transformable(pinchToZoom)
            ) {
                val (map, selectedObjective) = createRefs()
                ShowGridData(Modifier.constrainAs(map) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })

                // Overlay the selected objective over everything else on the map.
                ShowSelectedObjective(Modifier.constrainAs(selectedObjective) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                })
            }
        }

        // Display a progress bar until tiling is finished.
        if (grid.tiles.isEmpty() || grid.tiles.size < contentSize) {
            ShowMissingGridData()
        }
    }

    /**
     * Displays content related to the grid data not being populated.
     */
    @Preview
    @Composable
    private fun ShowMissingGridData() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ProgressIndicator()
        }

        // Attempt to rectify the missing data.
        SideEffect {
            CoroutineScope(Dispatchers.IO).launch {
                gw2Cache.lockedInstance {
                    refreshGridData()
                }
            }
        }
    }

    /**
     * Displays the grid content.
     */
    @Composable
    private fun ShowGridData(modifier: Modifier) {
        val horizontal = rememberScrollState()
        val vertical = rememberScrollState()

        Box(
            modifier = modifier
                .fillMaxSize()
                .horizontalScroll(horizontal)
                .verticalScroll(vertical)
        ) {
            ShowMap()
            ShowObjectives()

            if (configuration.wvw.bloodlust.enabled) {
                ShowBloodlust()
            }
        }

        if (configuration.wvw.map.scroll.enabled) {
            InitialMapScroll(horizontal, vertical)
        }
    }

    /**
     * Scrolls the map to the configured WvW map within the grid.
     */
    @Composable
    private fun InitialMapScroll(horizontal: ScrollState, vertical: ScrollState) {
        val initial = remember { mutableStateOf(true) }
        if (!initial.value) return

        // Can't scale without knowing the continent dimensions and floor regions/maps.
        val floor = remember { floor }.value
        val continent = remember { continent }.value
        val grid = remember { grid }.value
        if (continent != null && floor != null && grid.tiles.isNotEmpty()) {
            // Get the WvW region. It should be the only one that exists within this floor.
            val region = floor.regions.values.firstOrNull { region -> region.name == configuration.wvw.map.regionName }

            // Scroll over to the configured map.
            region?.maps?.values?.firstOrNull { map -> map.name == configuration.wvw.map.scroll.mapName }?.let { eb ->
                val topLeft = eb.continentRectangle().point1
                val scaled = grid.scale(topLeft.x.toInt(), topLeft.y.toInt())
                rememberCoroutineScope().launch {
                    horizontal.animateScrollTo(scaled.first)
                    vertical.animateScrollTo(scaled.second)
                    initial.value = false
                }
            }
        }
    }

    /**
     * Displays the map represented by a tiled grid.
     */
    @Composable
    private fun ShowMap() = Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val grid = remember { grid }.value
        for (row in grid.rows) {
            Row {
                for (tile in row) {
                    ShowTile(tile, grid)
                }
            }
        }
    }

    /**
     * Displays an individual tile.
     */
    @Composable
    private fun ShowTile(tile: Tile, grid: TileGrid) {
        val density = LocalDensity.current
        val tileContent = remember { tileContent }

        // Need to specify non-zero width/height on the default bitmap.
        val bitmap = tileContent[tile] ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        Image(
            painter = BitmapPainter(bitmap.asImageBitmap()),
            contentDescription = "WvW Map",
            modifier = Modifier
                .size(density.run { grid.tileWidth.toDp() }, density.run { grid.tileHeight.toDp() })
                .clickable(
                    // Disable the ripple so that the illusion of a contiguous map is not broken.
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // Clear the objective pop-up.
                    selectedObjective.value = null
                }
        )
    }

    /**
     * Displays the objectives on the map.
     */
    @Composable
    private fun ShowObjectives() {
        // Render from bottom right to top left.
        val comparator = compareByDescending<WvwObjective> { objective -> objective.coordinates().y }.thenByDescending { objective -> objective.coordinates().x }
        remember { objectives }.value.sortedWith(comparator).forEach { objective ->
            ShowObjective(objective)
        }
    }

    /**
     * Displays the individual objective.
     */
    @OptIn(ExperimentalFoundationApi::class, ExperimentalTime::class)
    @Composable
    private fun ShowObjective(objective: WvwObjective) {
        val match = remember { match }.value

        // Find the objective through the match in order to find out who the owner is.
        val matchObjective = match.objective(objective) ?: return
        val owner = matchObjective.owner() ?: ObjectiveOwner.NEUTRAL

        val configObjective = configObjective(objective)
        val coordinates = scaledCoordinates(objective)
        val size = objectiveSize(objective)

        // Use a default link when the icon link doesn't exist. The link won't exist for atypical types such as Spawn/Mercenary.
        val link = if (objective.iconLink.isNotBlank()) objective.iconLink else configObjective?.defaultIconLink
        val request = ImageRequest.Builder(LocalContext.current)
            .data(link)
            .size(size.width.toInt(), size.height.toInt())
            .transformations(OwnedColorTransformation(configuration.wvw, owner))
            .build()

        // Measurements are done with DP so conversion must be done from pixels.
        // TODO extension(s)
        val density = LocalDensity.current
        val xDp = density.run { coordinates.x.toInt().toDp() }
        val yDp = density.run { coordinates.y.toInt().toDp() }
        val widthDp = density.run { size.width.toInt().toDp() }
        val heightDp = density.run { size.height.toInt().toDp() }

        // Overlay the objective image onto the map image.
        ConstraintLayout(
            modifier = Modifier
                .absoluteOffset(xDp, yDp)
                .wrapContentSize()
        ) {
            val (icon, timer, upgradeIndicator, claimIndicator, waypointIndicator) = createRefs()
            Image(
                painter = rememberImagePainter(request, imageLoader),
                contentDescription = objective.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .constrainAs(icon) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .size(widthDp, heightDp)
                    .combinedClickable(onLongClick = {
                        // Swap pages to display all of the information instead of the limited information that normally comes with the pop-up.
                        selectedObjective.value = objective
                        selectedPage.value = DETAILED_SELECTED_OBJECTIVE
                    }) {
                        selectedObjective.value = objective
                    }
            )

            // Need to do the constraining within the scope of the ConstraintLayout.
            if (configuration.wvw.objectives.progressions.enabled) {
                val progression = getProgression(objective.upgradeId, matchObjective.yaksDelivered)
                progression?.iconLink?.let { iconLink ->
                    val upgradeSize = progression.size ?: configuration.wvw.objectives.progressions.defaultSize
                    ShowIndicator(iconLink, upgradeSize, "Upgraded", Modifier.constrainAs(upgradeIndicator) {
                        // Display the indicator in the top center of the objective icon.
                        top.linkTo(icon.top)
                        start.linkTo(icon.start)
                        end.linkTo(icon.end)
                    })
                }
            }

            if (configuration.wvw.objectives.claim.enabled && !matchObjective.claimedBy.isNullOrBlank()) {
                configuration.wvw.objectives.claim.iconLink?.let { iconLink ->
                    ShowIndicator(iconLink, configuration.wvw.objectives.claim.size, "Guild Claimed", Modifier.constrainAs(claimIndicator) {
                        // Display the indicator in the bottom right of the objective icon.
                        bottom.linkTo(icon.bottom)
                        end.linkTo(icon.end)
                    })
                }
            }

            if (configuration.wvw.objectives.waypoint.enabled) {
                ShowWaypointIndicator(objective, matchObjective, Modifier.constrainAs(waypointIndicator) {
                    // Display the indicator in the bottom left of the objective icon.
                    bottom.linkTo(icon.bottom)
                    start.linkTo(icon.start)
                })
            }

            if (configuration.wvw.objectives.immunity.enabled) {
                val immunity = configObjective?.immunity ?: configuration.wvw.objectives.immunity.defaultDuration
                val flippedAt = matchObjective.lastFlippedAt
                if (immunity != null && flippedAt != null) {
                    // Display the timer underneath the objective icon.
                    ShowImmunityTimer(immunity, flippedAt, Modifier.constrainAs(timer) {
                        top.linkTo(icon.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    })
                }
            }
        }
    }

    /**
     * Displays an indicator.
     */
    @Composable
    private fun ShowIndicator(
        iconLink: String,
        size: com.bselzer.gw2.manager.configuration.common.Size,
        contentDescription: String,
        modifier: Modifier,
        transformations: List<Transformation> = emptyList()
    ) {
        val request = ImageRequest.Builder(LocalContext.current)
            .data(iconLink)
            .size(size.width, size.height)
            .transformations(transformations)
            .build()

        // Measurements are done with DP so conversion must be done from pixels.
        val density = LocalDensity.current
        val widthDp = density.run { size.width.toDp() }
        val heightDp = density.run { size.height.toDp() }

        Image(
            painter = rememberImagePainter(request, imageLoader),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = modifier.size(widthDp, heightDp)
        )
    }

    /**
     * Displays an indicator for an objective that has a waypoint upgrade.
     */
    @Composable
    private fun ShowWaypointIndicator(objective: WvwObjective, matchObjective: WvwMapObjective, modifier: Modifier) {
        val waypoint = configuration.wvw.objectives.waypoint
        val iconLink = waypoint.iconLink ?: return
        val upgrades = remember { upgrades }.value
        val guildUpgrades = remember { guildUpgrades }.value
        val transformations = mutableListOf<Transformation>()

        // Verify that the objective has been upgraded to a tier that has the waypoint upgrade.
        val upgrade = upgrades[objective.upgradeId]
        val tierUpgrades = upgrade?.tiers?.filter { tier -> matchObjective.yaksDelivered >= tier.yaksRequired }?.flatMap { tier -> tier.upgrades } ?: emptyList()
        if (!tierUpgrades.any { tierUpgrade -> waypoint.upgradeNameRegex.matches(tierUpgrade.name) }) {
            // Fallback to trying to find the tactic.
            if (!waypoint.guild.enabled || !matchObjective.guildUpgradeIds.mapNotNull { id -> guildUpgrades[id] }.any { tactic -> waypoint.guild.upgradeNameRegex.matches(tactic.name) }) {
                // No upgrade or tactic waypoint so do not display anything.
                return
            }

            // Change the color of the waypoint to indicate that the tactic is available for use (and thus not permanent which the upgrade is).
            transformations.add(HexColorTransformation(configuration.wvw.objectives.waypoint.guild.color))
        }

        ShowIndicator(iconLink, size = configuration.wvw.objectives.waypoint.size, contentDescription = "Waypoint", modifier = modifier, transformations = transformations)
    }

    /**
     * Displays the immunity timer for a recently captured objective.
     */
    @OptIn(ExperimentalTime::class)
    @Composable
    private fun ShowImmunityTimer(immunity: Duration, flippedAt: Instant, modifier: Modifier) {
        val remaining = immunity - Clock.System.now().minus(flippedAt)
        if (remaining.isNegative()) return

        var countdown by remember { mutableStateOf(Int.MIN_VALUE) }
        val totalSeconds = remaining.inWholeSeconds
        val seconds: Int = (totalSeconds % 60).toInt()
        val minutes: Int = (totalSeconds / 60).toInt()

        // Formatting: https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html
        Text(
            text = "%01d:%02d".format(minutes, seconds),
            fontWeight = FontWeight.Bold,
            fontSize = configuration.wvw.objectives.immunity.textSize.sp,
            color = androidx.compose.ui.graphics.Color.White,
            modifier = modifier.wrapContentSize()
        )

        LaunchedEffect(key1 = countdown) {
            // Advance the countdown.
            delay(Duration.milliseconds(configuration.wvw.objectives.immunity.delay))
            countdown += 1
        }
    }

    /**
     * Displays general information about the objective the user clicked on in a pop-up label.
     */
    @Composable
    private fun ShowSelectedObjective(modifier: Modifier) {
        val selected = configuration.wvw.objectives.selected
        val selectedObjective = remember { selectedObjective }.value ?: return
        val match = remember { match }.value
        val matchObjective = match.objective(selectedObjective)
        val owner = matchObjective?.owner() ?: ObjectiveOwner.NEUTRAL
        val title = "${selectedObjective.name} (${owner.userFriendly()} ${selectedObjective.type})"

        Box(
            modifier = modifier.wrapContentSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.gw2_ice),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
            Column(
                modifier = Modifier.wrapContentSize()
            ) {
                val textSize = selected.textSize.sp
                Text(text = title, fontSize = textSize, fontWeight = FontWeight.Bold)
                matchObjective?.let { matchObjective ->
                    matchObjective.lastFlippedAt?.let { lastFlippedAt ->
                        // TODO kotlinx.datetime please support formatting
                        val localDate = lastFlippedAt.toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime()
                        Text(text = "Flipped at ${selected.dateFormatter.format(localDate)}", fontSize = textSize)
                    }
                }
            }
        }
    }

    /**
     * Displays the bloodlust icon within each borderland.
     */
    @Composable
    private fun ShowBloodlust() {
        val bloodlust = configuration.wvw.bloodlust
        val match = remember { match }.value ?: return
        val objectives = remember { objectives }.value

        val width = bloodlust.size.width
        val height = bloodlust.size.height

        val borderlands = match.maps.filter { map -> map.type().isOneOf(MapType.BLUE_BORDERLANDS, MapType.RED_BORDERLANDS, MapType.GREEN_BORDERLANDS) }
        for (borderland in borderlands) {
            // Use the center of all of the ruins as the position of the bloodlust icon.
            val matchRuins = borderland.objectives.filter { objective -> objective.type() == ObjectiveType.RUINS }
            if (matchRuins.isEmpty()) {
                Timber.w("Unable to create the bloodlust icon when there are no ruins on map ${borderland.id}.")
                continue
            }

            val objectiveRuins = matchRuins.mapNotNull { ruin -> objectives.firstOrNull { objective -> objective.id == ruin.id } }
            if (objectiveRuins.count() != matchRuins.count()) {
                Timber.w("Mismatch between the number of ruins in the match and objectives.")
                continue
            }

            val owner = borderland.bonuses.firstOrNull { bonus -> bonus.type() == MapBonusType.BLOODLUST }?.owner() ?: ObjectiveOwner.NEUTRAL
            val request = ImageRequest.Builder(LocalContext.current)
                .data(bloodlust.iconLink)
                .size(width, height)
                .transformations(OwnedColorTransformation(configuration.wvw, owner))
                .build()

            // Scale the position before using it.
            val x = objectiveRuins.sumOf { ruin -> ruin.coordinates().x } / objectiveRuins.count()
            val y = objectiveRuins.sumOf { ruin -> ruin.coordinates().y } / objectiveRuins.count()
            val coordinates = Point2D(x, y).scaledCoordinates(this.grid.value, Dimension2D(width.toDouble(), height.toDouble()))

            // Measurements are done with DP so conversion must be done from pixels.
            val density = LocalDensity.current
            val xDp = density.run { coordinates.x.toInt().toDp() }
            val yDp = density.run { coordinates.y.toInt().toDp() }
            val widthDp = density.run { width.toDp() }
            val heightDp = density.run { height.toDp() }

            Image(
                painter = rememberImagePainter(request, imageLoader),
                contentDescription = "Bloodlust",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .absoluteOffset(xDp, yDp)
                    .size(widthDp, heightDp)
            )
        }
    }

    /**
     * Displays the app bar for the [Page.MAP].
     */
    @Composable
    private fun ShowMapAppBar() {
        val scope = rememberCoroutineScope()
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.activity_wvw), fontWeight = FontWeight.Bold) },
            navigationIcon = {
                UpNavigationIcon(Intent(this@WvwActivity, MainActivity::class.java))
            },
            actions = {
                IconButton(onClick = { scope.launch { refreshData() } }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                }

                Box {
                    var isExpanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { isExpanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More Options")
                    }

                    DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                        // Only enable zoom in/zoom out buttons when they can be used.
                        val zoom = remember { zoom }.value
                        val min = configuration.wvw.map.zoom.min
                        val max = configuration.wvw.map.zoom.max

                        IconButton(enabled = zoom < max, onClick = {
                            changeZoom(increment = 1)
                            isExpanded = false
                        }) {
                            Icon(painter = painterResource(id = R.drawable.ic_zoom_in), contentDescription = "Zoom In")
                        }

                        IconButton(enabled = zoom > min, onClick = {
                            changeZoom(increment = -1)
                            isExpanded = false
                        }) {
                            Icon(painter = painterResource(id = R.drawable.ic_zoom_out), contentDescription = "Zoom Out")
                        }
                        IconButton(onClick = { showSelectWorldDialog() }) {
                            Icon(Icons.Filled.List, contentDescription = "World")
                        }
                    }
                }
            }
        )
    }

    /**
     * Updates the zoom to be within the configured range.
     */
    private fun changeZoom(increment: Int) {
        val currentZoom = this.zoom.value
        val min = configuration.wvw.map.zoom.min
        val max = configuration.wvw.map.zoom.max
        this.zoom.value = max(min, min(max, currentZoom + increment))
    }

    // endregion ShowMap

    // region Objective Helpers
    /**
     * @return the progression level associated with the upgrade associated with the objective
     */
    @Composable
    private fun getProgression(upgradeId: Int, yaksDelivered: Int): WvwUpgradeProgression? {
        val upgrades = remember { upgrades }.value
        val upgrade = upgrades[upgradeId] ?: return null
        val level = upgrade.tiers.count { tier -> yaksDelivered >= tier.yaksRequired }
        return configuration.wvw.objectives.progressions.progression.getOrNull(level - 1)
    }


    /**
     * @return the objective from the configuration that matches the objective from the objectives endpoint
     */
    private fun configObjective(objective: WvwObjective): com.bselzer.gw2.manager.configuration.wvw.WvwObjective? {
        val type = objective.type()
        return configuration.wvw.objectives.objectives.firstOrNull { configObjective -> configObjective.type == type }
    }

    /**
     * @return the size of the image associated with an objective
     */
    private fun objectiveSize(objective: WvwObjective): Dimension2D {
        val configObjective = configObjective(objective)

        // Get the size from the configured objective if it is defined, otherwise use the default.
        val width = configObjective?.size?.width ?: configuration.wvw.objectives.defaultSize.width
        val height = configObjective?.size?.height ?: configuration.wvw.objectives.defaultSize.height
        return Dimension2D(width.toDouble(), height.toDouble())
    }

    /**
     * @return the scaled coordinates of the image associated with an objective
     */
    @Composable
    private fun scaledCoordinates(objective: WvwObjective): Point2D {
        val grid = remember { grid }.value

        // Use the explicit coordinates if they exist, otherwise default to the label coordinates. This is needed for atypical types such as Spawn/Mercenary.
        val coordinates = objective.position()

        // Scale the objective coordinates to the zoom level and remove excluded bounds.
        return coordinates.scaledCoordinates(grid, objectiveSize(objective))
    }

    /**
     * @return the scaled coordinates of the image
     */
    private fun Point2D.scaledCoordinates(grid: TileGrid, size: Dimension2D): Point2D =
        // Scale the objective coordinates to the zoom level and remove excluded bounds.
        grid.scale(x.toInt(), y.toInt()).run {
            // Displace the coordinates so that it aligns with the center of the image.
            copy(x = first - size.width / 2, y = second - size.height / 2)
        }

    // endregion Objective Helpers

    // region ShowMatch

    /**
     * Displays the page related to match details.
     */
    @Composable
    private fun ShowMatchPage() {
        // TODO match details: scores, ppt, etc
        ShowBackground(drawableId = R.drawable.gw2_ice)
    }

    // endregion ShowMatch

    // region ShowDetailedSelectedObjective

    /**
     * Displays the page related to detailed objective information.
     */
    @Composable
    private fun ShowDetailedSelectedObjectivePage() {
        // TODO show specifics: claimed by, upgrades/tactics/improvements, etc
        ShowBackground(drawableId = R.drawable.gw2_ice)
    }

    // endregion ShowDetailedSelectedObjective

    // region ShowCommon
    /**
     * Create a dialog for the user to select the world.
     */
    private fun showSelectWorldDialog(cancellable: Boolean = true) {
        val worlds = this.worlds.value.sortedBy { world -> world.name }.toList()
        if (worlds.isEmpty()) {
            Toast.makeText(this, "Waiting for the worlds to be downloaded.", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val selectedId = datastore.nullLatest(SELECTED_WORLD)
            Timber.d("Selected world id: $selectedId")

            // If there is no matching world then the resulting -1 will specify no selection.
            val selectedWorld = worlds.indexOfFirst { world -> world.id == selectedId }
            withContext(Dispatchers.Main)
            {
                AlertDialog.Builder(this@WvwActivity)
                    .setTitle("Worlds")
                    .setSingleChoiceItems(worlds.map { world -> world.name }.toTypedArray(), selectedWorld) { dialog, which ->
                        CoroutineScope(Dispatchers.IO).launch {
                            datastore.update(SELECTED_WORLD, worlds[which].id)
                            refreshData()
                        }
                        dialog.dismiss()
                    }
                    .setCancelable(cancellable)
                    .show()
            }
        }
    }

    override fun onBackPressed() {
        when (selectedPage.value) {
            MAP -> selectedPage.value = null // Go back to the menu.
            MATCH -> selectedPage.value = null // Go back to the menu.
            DETAILED_SELECTED_OBJECTIVE -> {
                // Go back to the map page with the objective cleared so that the pop-up is not displayed.
                selectedPage.value = MAP
                selectedObjective.value = null
            }
            null -> finish()
        }
    }

    // endregion ShowCommon
}