package com.bselzer.gw2.manager.android.ui.activity.wvw

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.Transformation
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.ui.activity.common.BaseActivity
import com.bselzer.gw2.manager.android.ui.activity.main.MainActivity
import com.bselzer.gw2.manager.android.ui.activity.wvw.WvwActivity.Page.*
import com.bselzer.gw2.manager.android.ui.coil.HexColorTransformation
import com.bselzer.gw2.manager.common.configuration.common.Size
import com.bselzer.gw2.manager.common.configuration.wvw.WvwUpgradeProgression
import com.bselzer.library.gw2.v2.cache.instance.ContinentCache
import com.bselzer.library.gw2.v2.cache.instance.GuildCache
import com.bselzer.library.gw2.v2.cache.instance.WorldCache
import com.bselzer.library.gw2.v2.cache.instance.WvwCache
import com.bselzer.library.gw2.v2.emblem.request.EmblemRequestOptions
import com.bselzer.library.gw2.v2.model.continent.Continent
import com.bselzer.library.gw2.v2.model.continent.ContinentFloor
import com.bselzer.library.gw2.v2.model.enumeration.extension.wvw.mapType
import com.bselzer.library.gw2.v2.model.enumeration.extension.wvw.owner
import com.bselzer.library.gw2.v2.model.enumeration.extension.wvw.type
import com.bselzer.library.gw2.v2.model.enumeration.wvw.MapBonusType
import com.bselzer.library.gw2.v2.model.enumeration.wvw.MapType
import com.bselzer.library.gw2.v2.model.enumeration.wvw.ObjectiveOwner
import com.bselzer.library.gw2.v2.model.enumeration.wvw.ObjectiveType
import com.bselzer.library.gw2.v2.model.extension.continent.continentRectangle
import com.bselzer.library.gw2.v2.model.extension.wvw.*
import com.bselzer.library.gw2.v2.model.guild.Guild
import com.bselzer.library.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.library.gw2.v2.model.world.World
import com.bselzer.library.gw2.v2.model.wvw.match.WvwMapObjective
import com.bselzer.library.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.library.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.library.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.library.gw2.v2.tile.model.response.Tile
import com.bselzer.library.gw2.v2.tile.model.response.TileGrid
import com.bselzer.library.kotlin.extension.compose.effect.PreRepeatedEffect
import com.bselzer.library.kotlin.extension.compose.ui.appbar.MaterialAppBar
import com.bselzer.library.kotlin.extension.compose.ui.appbar.RefreshIcon
import com.bselzer.library.kotlin.extension.compose.ui.appbar.UpNavigationIcon
import com.bselzer.library.kotlin.extension.compose.ui.container.DividedColumn
import com.bselzer.library.kotlin.extension.compose.ui.dialog.SingleChoiceDialog
import com.bselzer.library.kotlin.extension.compose.ui.geometry.ArcShape
import com.bselzer.library.kotlin.extension.compose.ui.unit.toDp
import com.bselzer.library.kotlin.extension.function.collection.isOneOf
import com.bselzer.library.kotlin.extension.function.objects.userFriendly
import com.bselzer.library.kotlin.extension.geometry.dimension.bi.Dimension2D
import com.bselzer.library.kotlin.extension.geometry.dimension.bi.position.Point2D
import com.bselzer.library.kotlin.extension.logging.Logger
import com.bselzer.library.kotlin.extension.settings.compose.safeState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class WvwActivity : BaseActivity() {
    private val worlds = mutableStateOf<Collection<World>>(emptyList())
    private val match = mutableStateOf<WvwMatch?>(null)
    private val objectives = mutableStateOf<Collection<WvwObjective>>(emptyList())
    private val upgrades = mutableStateOf(emptyMap<Int, WvwUpgrade>())
    private val guildUpgrades = mutableStateOf(emptyMap<Int, GuildUpgrade>())
    private val guilds = mutableStateMapOf<String, Guild>()
    private val continent = mutableStateOf<Continent?>(null)
    private val floor = mutableStateOf<ContinentFloor?>(null)
    private val grid = mutableStateOf(TileGrid())
    private val selectedObjective = mutableStateOf<WvwObjective?>(null)
    private val tileContent = mutableStateMapOf<Tile, Bitmap>()
    private val zoom = mutableStateOf(0)
    private val selectedPage = mutableStateOf<Page?>(null)
    private val showWorldDialog = mutableStateOf(false)

    private enum class Page {
        MAP,
        MATCH,
        DETAILED_SELECTED_OBJECTIVE
    }

    /**
     * The number of tiles with content and the total number of tiles for the current zoom level.
     */
    private data class TileCount(val contentSize: Int, val gridSize: Int) {
        val isEmpty: Boolean = gridSize == 0 || contentSize == 0
        val hasAllContent: Boolean = contentSize >= gridSize
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        zoom.value = configuration.wvw.map.zoom.default
        super.onCreate(savedInstanceState)
    }

    // region Refresh
    /**
     * Refreshes the WvW data.
     */
    private suspend fun refreshData(selectedWorld: Int) = withContext(Dispatchers.IO) {
        Logger.d("Refreshing WvW data for world ${selectedWorld}.")

        gw2Cache.lockedInstance {
            worlds.value = get<WorldCache>().findWorlds()

            // Need the world to be able to get the associated match.
            if (selectedWorld <= 0) {
                showWorldDialog.value = true
                return@lockedInstance
            }

            val cache = get<WvwCache>()
            val match = gw2Client.wvw.match(selectedWorld)
            cache.putMatch(match)
            val objectives = cache.findObjectives(match)

            this@WvwActivity.match.value = match
            this@WvwActivity.objectives.value = objectives
            this@WvwActivity.upgrades.value = cache.findUpgrades(objectives).associateBy { it.id }
            this@WvwActivity.guildUpgrades.value = cache.findGuildUpgrades(objectives.mapNotNull { objective -> match.objective(objective) }).associateBy { it.id }

            // Map refresh is comparatively expensive so only do it when the user is on its page.
            if (selectedPage.value == MAP) {
                refreshMapData(match)
                refreshGridData()
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
            val cache = get<ContinentCache>()
            val continent = cache.getContinent(configuration.wvw.map.continentId)
            this@WvwActivity.floor.value = cache.getContinentFloor(configuration.wvw.map.continentId, configuration.wvw.map.floorId)
            this@WvwActivity.continent.value = continent
        }
    }

    /**
     * Refreshes the WvW map data using a map found from the match.
     */
    private suspend fun refreshMapData(match: WvwMatch) = withContext(Dispatchers.IO) {
        gw2Cache.instance {
            Logger.d("Refreshing WvW map data.")

            // Assume that all WvW maps are within the same continent and floor.
            val mapId = match.maps.firstOrNull()?.id ?: return@instance
            val cache = get<ContinentCache>()
            val map = cache.getMap(mapId)
            val continent = cache.getContinent(map)
            this@WvwActivity.floor.value = cache.getContinentFloor(map)
            this@WvwActivity.continent.value = continent
        }
    }

    /**
     * Refreshes the WvW map tiling grid.
     */
    private suspend fun refreshGridData() = withContext(Dispatchers.IO) {
        gw2Cache.instance {
            val continent = continent.value
            val floor = floor.value

            // Verify that the related data exists.
            if (continent == null || floor == null) {
                return@instance
            }

            val zoom = zoom.value
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
            grid.value = TileGrid(gridRequest, gridRequest.tileRequests.map { tileRequest -> Tile(tileRequest) })

            // Defer the content for parallelism and populate it when its ready.
            for (deferred in tileCache.findTilesAsync(gridRequest.tileRequests)) {
                val tile = deferred.await()
                val bitmap = BitmapFactory.decodeByteArray(tile.content, 0, tile.content.size)
                tileContent[tile] = bitmap
            }
        }
    }

    /**
     * Refreshes the guild data for the [id].
     */
    private suspend fun refreshGuild(id: String?) = withContext(Dispatchers.IO) {
        if (id.isNullOrBlank()) {
            // Skip refreshing bad ids.
            return@withContext
        }

        guilds[id] = gw2Cache.instance {
            get<GuildCache>().getGuild(id)
        }
    }
    // endregion Refresh

    // region ShowMenu

    @OptIn(ExperimentalTime::class)
    @Composable
    override fun Content() = app.Content {
        val selectedPage = rememberSaveable { selectedPage }.value
        when (selectedPage) {
            MAP -> ShowMapPage()
            MATCH -> ShowMatchPage()
            DETAILED_SELECTED_OBJECTIVE -> ShowSelectedObjectivePage()
            null -> ShowMenu()
        }

        if (remember { showWorldDialog }.value) {
            SelectWorldDialog()
        }

        // Map refresh is comparatively expensive so only do it when the user is on its page.
        LaunchedEffect(selectedPage) {
            if (selectedPage == MAP) {
                refreshMapData()
                refreshGridData()
            }
        }

        PreRepeatedEffect(delay = runBlocking { wvwPref.refreshInterval.get() }) {
            refreshData(wvwPref.selectedWorld.get())
        }

        LaunchedEffect(key1 = zoom.value) {
            refreshGridData()
        }
    }

    /**
     * Displays the World vs. World menu.
     */
    @Composable
    private fun ShowMenu() = Column {
        // Match the same way that the MainActivity is displayed.
        MaterialAppBar(title = stringResource(R.string.activity_wvw), navigationIcon = { UpNavigationIcon(MainActivity::class.java) })
        AbsoluteBackground {
            ShowMenu(
                stringResource(id = R.string.wvw_map) to { selectedPage.value = MAP },
                stringResource(id = R.string.wvw_match) to { selectedPage.value = MATCH }
            )
        }
    }

    // endregion ShowMenu

    // region ShowMap

    @Composable
    private fun ShowMapPage() {
        // Display the background until tiling occurs.
        val tileCount = tileCount()
        if (tileCount.isEmpty) {
            AbsoluteBackground { }
        }

        Column {
            ShowMapAppBar()

            val pinchToZoom = rememberTransformableState { zoomChange, _, _ ->
                // Allow the user to change the zoom by pinching the map.
                val change = if (zoomChange > 1) 1 else -1
                changeZoom(change)
            }

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .transformable(pinchToZoom)
            ) {
                val (map, selectedObjective) = createRefs()
                ShowGridData(Modifier.constrainAs(map) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })

                // Overlay the selected objective over everything else on the map.
                ShowSelectedObjectiveLabel(Modifier.constrainAs(selectedObjective) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                })

                // TODO show map names (borderlands preferably with team names)
            }
        }

        // Display a progress bar until tiling is finished.
        if (tileCount.isEmpty || !tileCount.hasAllContent) {
            ShowMissingGridData()
        }
    }

    /**
     * Displays content related to the grid data not being populated.
     */
    @Composable
    private fun ShowMissingGridData() = Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ShowProgressIndicator()
    }

    /**
     * Displays the grid content.
     */
    @Composable
    private fun ShowGridData(modifier: Modifier) {
        val horizontal = rememberScrollState()
        val vertical = rememberScrollState()
        val tileCount = tileCount()
        Box(
            modifier = modifier
                .fillMaxSize()
                .horizontalScroll(horizontal)
                .verticalScroll(vertical)
        ) {
            ShowMap()
            ShowObjectives()

            if (configuration.wvw.bloodlust.enabled && tileCount.hasAllContent) {
                ShowBloodlust()
            }
        }

        if (configuration.wvw.map.scroll.enabled && !tileCount.isEmpty) {
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
        if (continent != null && floor != null) {
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
        // Render from bottom right to top left so that overlap is consistent.
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

        // Overlay the objective image onto the map image.
        ConstraintLayout(
            modifier = Modifier
                .absoluteOffset(coordinates.x.toDp(), coordinates.y.toDp())
                .wrapContentSize()
        ) {
            val (icon, timer, upgradeIndicator, claimIndicator, waypointIndicator) = createRefs()
            Image(
                painter = objectiveImagePainter(objective, owner),
                contentDescription = objective.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .constrainAs(icon) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .size(size.width.toDp(), size.height.toDp())
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
        size: Size,
        contentDescription: String,
        modifier: Modifier,
        transformations: List<Transformation> = emptyList()
    ) {
        val request = ImageRequest.Builder(LocalContext.current)
            .data(iconLink)
            .size(size.width, size.height)
            .transformations(transformations)
            .build()

        Image(
            painter = rememberImagePainter(request, imageLoader),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = modifier.size(size.width.toDp(), size.height.toDp())
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
        val tierUpgrades = upgrade?.tiers(yaksDelivered = matchObjective.yaksDelivered)?.flatMap { tier -> tier.upgrades } ?: emptyList()
        if (!tierUpgrades.any { tierUpgrade -> waypoint.upgradeNameRegex.matches(tierUpgrade.name) }) {
            // Fallback to trying to find the tactic.
            if (!waypoint.guild.enabled || !matchObjective.guildUpgradeIds.mapNotNull { id -> guildUpgrades[id] }
                    .any { tactic -> waypoint.guild.upgradeNameRegex.matches(tactic.name) }) {
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

        // If the time has finished or the current time is incorrectly set and thus causing an inflated remaining time, do not display it.
        // For the latter case, while the timers shown will be incorrect they will at the very least not be inflated.
        if (remaining.isNegative() || remaining > immunity) return

        var countdown by remember { mutableStateOf(Int.MIN_VALUE) }
        val totalSeconds = remaining.inWholeSeconds
        val seconds: Int = (totalSeconds % 60).toInt()
        val minutes: Int = (totalSeconds / 60).toInt()

        // Formatting: https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html
        Text(
            text = "%01d:%02d".format(minutes, seconds),
            fontWeight = FontWeight.Bold,
            fontSize = configuration.wvw.objectives.immunity.textSize.sp,
            color = Color.White,
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
    private fun ShowSelectedObjectiveLabel(modifier: Modifier) {
        val selected = configuration.wvw.objectives.selected
        val selectedObjective = remember { selectedObjective }.value ?: return
        val match = remember { match }.value
        val matchObjective = match.objective(selectedObjective)
        val owner = matchObjective?.owner() ?: ObjectiveOwner.NEUTRAL
        val title = "${selectedObjective.name} (${owner.userFriendly()} ${selectedObjective.type})"

        RelativeBackgroundColumn(modifier = modifier) {
            val textSize = selected.textSize.sp
            Text(text = title, fontSize = textSize, fontWeight = FontWeight.Bold)
            matchObjective?.lastFlippedAt?.let { lastFlippedAt ->
                Text(text = "Flipped at ${lastFlippedAtFormatted(lastFlippedAt)}", fontSize = textSize)
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
                Logger.w("Unable to create the bloodlust icon when there are no ruins on map ${borderland.id}.")
                continue
            }

            val objectiveRuins = matchRuins.mapNotNull { ruin -> objectives.firstOrNull { objective -> objective.id == ruin.id } }
            if (objectiveRuins.count() != matchRuins.count()) {
                Logger.w("Mismatch between the number of ruins in the match and objectives.")
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

            Image(
                painter = rememberImagePainter(request, imageLoader),
                contentDescription = "Bloodlust",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .absoluteOffset(coordinates.x.toDp(), coordinates.y.toDp())
                    .size(width.toDp(), height.toDp())
            )
        }
    }

    /**
     * Displays the app bar for the [Page.MAP].
     */
    @Composable
    private fun ShowMapAppBar() = ShowContentTopAppBar(title = R.string.wvw_map) {
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
            }
        }
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

    /**
     * @return the number of tiles with content mapped to the total number of tiles for the current zoom level
     */
    @Composable
    private fun tileCount(): TileCount {
        val grid = remember { grid }.value
        val tileContent = remember { tileContent }
        val zoom by zoom
        val contentSize = tileContent.filterKeys { key -> key.zoom == zoom }.size
        return TileCount(contentSize = contentSize, gridSize = grid.tiles.size)
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
        return configuration.wvw.objectives.progressions.progression.getOrNull(upgrade.level(yaksDelivered))
    }

    /**
     * @return the objective from the configuration that matches the objective from the objectives endpoint
     */
    private fun configObjective(objective: WvwObjective): com.bselzer.gw2.manager.common.configuration.wvw.WvwObjective? {
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

    /**
     * Remembers the image painter associated with the [objective].
     */
    @Composable
    private fun objectiveImagePainter(objective: WvwObjective, owner: ObjectiveOwner): ImagePainter {
        val size = objectiveSize(objective)
        val configObjective = configObjective(objective)

        // Use a default link when the icon link doesn't exist. The link won't exist for atypical types such as Spawn/Mercenary.
        val link = if (objective.iconLink.isNotBlank()) objective.iconLink else configObjective?.defaultIconLink
        val request = ImageRequest.Builder(LocalContext.current)
            .data(link)
            .size(size.width.toInt(), size.height.toInt())
            .transformations(OwnedColorTransformation(configuration.wvw, owner))
            .build()

        return rememberImagePainter(request, imageLoader)
    }

    /**
     * @return the [lastFlippedAt] date/time instant to a displayable formatted string
     */
    private fun lastFlippedAtFormatted(lastFlippedAt: Instant): String {
        // TODO kotlinx.datetime please support formatting
        val localDate = lastFlippedAt.toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime()
        return configuration.wvw.objectives.selected.dateFormatter.format(localDate)
    }

    /**
     * @return the displayable names for the linked worlds associated with the objective [owner]
     */
    @Composable
    private fun displayableLinkedWorlds(owner: ObjectiveOwner): String? {
        val worlds = remember { worlds }.value
        val match = remember { match }.value ?: return null
        val linkedWorlds = match.linkedWorlds(owner).mapNotNull { worldId -> worlds.firstOrNull { world -> world.id == worldId }?.name }

        // Make sure that the main world is first.
        val mainWorld = match.mainWorld(owner)?.run { worlds.firstOrNull { world -> world.id == this }?.name }
        val sortedWorlds = if (mainWorld == null) linkedWorlds else linkedWorlds.toMutableList().apply { remove(mainWorld); add(0, mainWorld) }
        return sortedWorlds.joinToString(separator = "/")
    }

    // endregion Objective Helpers

    // region ShowMatch

    /**
     * Displays the page related to match details.
     */
    @Composable
    private fun ShowMatchPage() = Column(modifier = Modifier.fillMaxSize()) {
        ShowContentTopAppBar(title = R.string.wvw_match)

        val match = remember { match }.value ?: return

        // TODO pager: main = total, then for each map (will need map name title on each page)
        AbsoluteBackground(modifier = Modifier.fillMaxSize()) {
            DividedColumn(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                divider = { Spacer(modifier = Modifier.height(5.dp)) },
                contents = arrayOf(
                    { ShowMatchChart(match.pointsPerTick(), "Points Per Tick") },
                    { ShowMatchChart(match.victoryPoints(), "Victory Points") },
                    { ShowMatchChart(match.scores(), "Total Score") },
                    { ShowMatchChart(match.kills(), "Total Kills") },
                    { ShowMatchChart(match.deaths(), "Total Deaths") }
                )
            )
        }
    }

    /**
     * Displays a pie chart for the [ObjectiveOwner.BLUE], [ObjectiveOwner.RED], and [ObjectiveOwner.GREEN].
     */
    @Composable
    private fun ShowMatchChart(data: Map<out ObjectiveOwner?, Int>, title: String) {
        val chart = configuration.wvw.chart
        val owners = listOf(ObjectiveOwner.BLUE, ObjectiveOwner.GREEN, ObjectiveOwner.RED)
        val total = data.filterKeys { owner -> owners.contains(owner) }.values.sum().toFloat()

        // Show the chart itself.
        Box {
            val blueAngle = data.calculateSliceAngle(total, ObjectiveOwner.BLUE)
            val greenAngle = data.calculateSliceAngle(total, ObjectiveOwner.GREEN)
            ShowMatchChartBackground()

            ShowMatchChartSlice(link = chart.blueLink, startAngle = 0f, endAngle = blueAngle)
            ShowMatchChartSlice(link = chart.greenLink, startAngle = blueAngle, endAngle = blueAngle + greenAngle)
            ShowMatchChartSlice(link = chart.redLink, startAngle = blueAngle + greenAngle, endAngle = 360f)

            ShowMatchChartDivider(angle = 0f)
            ShowMatchChartDivider(angle = blueAngle)
            ShowMatchChartDivider(angle = blueAngle + greenAngle)
        }

        RelativeBackgroundColumn(modifier = Modifier.fillMaxWidth()) {
            // Show the title.
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = chart.title.textSize.sp, textAlign = TextAlign.Center)

            // Show the associated data per owner.
            for (owner in owners) {
                val color = configuration.wvw.objectives.color(owner)
                val textSize = chart.data.textSize.sp
                val linkedWorlds = displayableLinkedWorlds(owner)
                Text(
                    text = if (linkedWorlds.isNullOrBlank()) owner.userFriendly() else linkedWorlds,
                    fontWeight = FontWeight.Bold,
                    fontSize = textSize,
                    color = color,
                    textAlign = TextAlign.Center
                )
                Text(text = (data[owner] ?: 0).toString(), fontSize = textSize, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(3.dp))
            }
        }
    }

    /**
     * @return the angle associated with the [owner]'s ratio of the [total]
     */
    private fun Map<out ObjectiveOwner?, Int>.calculateSliceAngle(total: Float, owner: ObjectiveOwner): Float {
        // Using float for total to avoid int division.
        return if (total <= 0) 120f else (this[owner] ?: 0) / total * 360f
    }

    /**
     * Displays the pie chart background.
     */
    @Composable
    private fun ShowMatchChartBackground() {
        val size = configuration.wvw.chart.size
        val shadow = ImageRequest.Builder(LocalContext.current)
            .data(configuration.wvw.chart.backgroundLink)
            .size(size.width, size.height)
            .build()

        Image(
            painter = rememberImagePainter(request = shadow),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(size.width.toDp(), size.height.toDp())
        )

        val neutral = ImageRequest.Builder(LocalContext.current)
            .data(configuration.wvw.chart.neutralLink)
            .size(size.width, size.height)
            .build()

        Image(
            painter = rememberImagePainter(request = neutral),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(size.width.toDp(), size.height.toDp())
        )
    }

    /**
     * Displays a slice of the pie chart.
     */
    @Composable
    private fun ShowMatchChartSlice(link: String, startAngle: Float, endAngle: Float) {
        val size = configuration.wvw.chart.size
        val request = ImageRequest.Builder(LocalContext.current)
            .data(link)
            .size(size.width, size.height)
            .build()

        Image(
            painter = rememberImagePainter(request = request),
            contentDescription = "Chart Slice",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(size.width.toDp(), size.height.toDp())
                .clip(ArcShape(startAngle, endAngle))
        )
    }

    /**
     * Displays a divider along the given [angle] to split pie chart slices.
     */
    @Composable
    private fun ShowMatchChartDivider(angle: Float) {
        val size = configuration.wvw.chart.size
        val request = ImageRequest.Builder(LocalContext.current)
            .data(configuration.wvw.chart.dividerLink)
            .size(size.width, size.height)
            .build()

        Image(
            painter = rememberImagePainter(request = request),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(size.width.toDp(), size.height.toDp())
                .rotate(angle)
        )
    }

    // endregion ShowMatch

    // region ShowDetailedSelectedObjective

    /**
     * Displays the page related to detailed objective information.
     */
    @Composable
    private fun ShowSelectedObjectivePage() = Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ShowContentTopAppBar(title = R.string.wvw_detailed_selected_objective)

        AbsoluteBackground(modifier = Modifier.fillMaxSize()) {
            // TODO pager: main = details, left = upgrades, right = guild upgrades
            ShowDetailedSelectedObjective()
        }

        remember { selectedObjective }.value?.let { objective ->
            remember { match }.value.objective(objective)?.let { matchObjective ->
                LaunchedEffect(matchObjective.claimedBy) {
                    refreshGuild(matchObjective.claimedBy)
                }
            }
        }
    }

    /**
     * Displays the detailed selected objective information.
     */
    @Composable
    private fun ShowDetailedSelectedObjective() = DividedColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        divider = { Spacer(modifier = Modifier.height(10.dp)) },
        prepend = true,
        append = true,
        contents = arrayOf(
            { ShowSelectedObjectiveImage() },
            { ShowSelectedObjectiveCard { ShowSelectedObjectiveOverview() } },
            { ShowSelectedObjectiveCard { ShowSelectedObjectivePoints() } },
            { ShowSelectedObjectiveClaimCard() }
        )
    )

    /**
     * Displays the image for the detailed selected objective.
     */
    @Composable
    private fun ShowSelectedObjectiveImage() {
        val objective = remember { selectedObjective }.value ?: return
        val match = remember { match }.value ?: return
        val matchObjective = match.objective(objective) ?: return

        Image(
            painter = objectiveImagePainter(objective, owner = matchObjective.owner() ?: ObjectiveOwner.NEUTRAL),
            contentDescription = objective.name,
            contentScale = ContentScale.Fit,

            // TODO objective images are mostly 32x32 and look awful as result of being scaled like this
            modifier = Modifier.size(64.dp, 64.dp)
        )
    }

    /**
     * Displays a card wrapping the underlying selected objective [content].
     */
    @Composable
    private fun ShowSelectedObjectiveCard(content: @Composable BoxScope.() -> Unit) {
        val border = 3.dp
        Card(
            elevation = 10.dp,
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth(.80f)
                .wrapContentHeight()
                .border(width = border, color = Color.Black)
                .padding(all = border)
        ) {
            RelativeBackground(content = content)
        }
    }

    /**
     * Displays the detailed overview information related to the selected objective.
     */
    @Composable
    private fun ShowSelectedObjectiveOverview() = Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // TODO images alongside the text?

        val objective = remember { selectedObjective }.value ?: return
        val match = remember { match }.value ?: return

        Text(text = "${objective.name} (${objective.type})", textAlign = TextAlign.Center)

        objective.mapType()?.let { mapType ->
            Text(text = mapType.userFriendly(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = configuration.wvw.objectives.color(mapType.owner()))
        }

        match.objective(objective)?.owner()?.let { owner ->
            val linkedWorlds = displayableLinkedWorlds(owner)
            if (!linkedWorlds.isNullOrBlank()) {
                Text(text = linkedWorlds, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = configuration.wvw.objectives.color(owner))
            }
        }

        match.objective(objective)?.lastFlippedAt?.let { lastFlippedAt ->
            Text(text = "Flipped at ${lastFlippedAtFormatted(lastFlippedAt)}", textAlign = TextAlign.Center)
        }
    }

    /**
     * Displays the point information related to the selected objective.
     */
    @Composable
    private fun ShowSelectedObjectivePoints() = Column {
        val objective = remember { selectedObjective }.value ?: return
        val matchObjective = remember { match }.value.objective(objective) ?: return

        BoldCenteredRow(startValue = "Points per tick:", endValue = "${matchObjective.pointsPerTick}")
        BoldCenteredRow(startValue = "Points per capture:", endValue = "${matchObjective.pointsPerCapture}")
        remember { upgrades }.value[objective.upgradeId]?.let { upgrade ->
            val yaksDelivered = matchObjective.yaksDelivered
            val ratio = upgrade.yakRatio(yaksDelivered)
            BoldCenteredRow(startValue = "Yaks delivered:", endValue = "${ratio.first}/${ratio.second}")

            val level = upgrade.level(yaksDelivered)
            val tier = upgrade.tier(yaksDelivered)?.name ?: "Not Upgraded"
            BoldCenteredRow(startValue = "Upgrade tier:", endValue = "$tier ($level/${upgrade.tiers.size})")
        }
    }

    /**
     * Displays the guild claim information related to the selected objective.
     */
    @Composable
    private fun ShowSelectedObjectiveClaimCard() {
        val objective = remember { selectedObjective }.value ?: return
        val matchObjective = remember { match }.value.objective(objective) ?: return
        val claimedAt = matchObjective.claimedAt ?: return

        // Note that claimedBy is the id, so it is necessary to look up the name from the guild model.
        val guildId = matchObjective.claimedBy ?: return
        val name = remember { guilds }[guildId]?.name
        if (name.isNullOrBlank()) return

        // Don't show the card at all if the related data doesn't exist.
        // Need to avoid having no children because an exception will get thrown because of recomposition trying to size nothing.
        ShowSelectedObjectiveCard {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // TODO separate formatter even if its the same as last flipped for now
                Text(text = "Claimed at ${lastFlippedAtFormatted(claimedAt)}", textAlign = TextAlign.Center)
                Text(text = "Claimed by $name", textAlign = TextAlign.Center)
                ShowGuildEmblem(guildId = guildId)
            }
        }
    }

    /**
     * Displays the guild emblem image.
     */
    @Composable
    private fun ShowGuildEmblem(guildId: String) {
        // Using max alpha on the background because they are too transparent without it.
        val size = 256
        val emblemRequest = emblemClient.requestEmblem(guildId = guildId, size = size, EmblemRequestOptions.MAXIMIZE_BACKGROUND_ALPHA)

        val coilRequest = ImageRequest.Builder(LocalContext.current)
            .data(emblemClient.emblemUrl(emblemRequest))
            .size(size = size)
            .build()

        Image(
            painter = rememberImagePainter(coilRequest, imageLoader),
            contentDescription = "Guild Emblem",
            modifier = Modifier.size(size = size.toDp())
        )
    }

    // endregion ShowDetailedSelectedObjective

    // region ShowCommon

    /**
     * Displays the top app bar used by the content pages.
     *
     * @param title the string resource id of the title
     * @param actions the supplementary actions
     */
    @Composable
    private fun ShowContentTopAppBar(@StringRes title: Int, actions: @Composable RowScope.() -> Unit = {}) = MaterialAppBar(
        title = stringResource(title),
        navigationIcon = { UpNavigationIcon(MainActivity::class.java) },
        actions = appBarActions(actions)
    )

    /**
     * Displays the common [ShowContentTopAppBar] icons for this activity.
     */
    @Composable
    private fun appBarActions(content: @Composable RowScope.() -> Unit = {}): @Composable RowScope.() -> Unit = {
        val selectedId by wvwPref.selectedWorld.safeState()
        RefreshIcon { refreshData(selectedId) }
        IconButton(onClick = { showWorldDialog.value = true }) {
            Icon(Icons.Filled.List, contentDescription = "World")
        }
        content()
    }

    /**
     * Create a dialog for the user to select the world.
     */
    @Composable
    private fun SelectWorldDialog() {
        val worlds = remember { worlds }.value.sortedBy { world -> world.name }
        if (worlds.isEmpty()) {
            Toast.makeText(this, "Waiting for the worlds to be downloaded.", Toast.LENGTH_SHORT).show()
            return
        }

        var selectedId by wvwPref.selectedWorld.safeState()
        Logger.d("Selected world id: $selectedId")

        val selected = remember { mutableStateOf<World?>(null) }
        selected.value = worlds.firstOrNull { world -> world.id == selectedId }

        // TODO preferably, choice should be scrolled to when dialog gets opened
        SingleChoiceDialog(
            showDialog = { showWorldDialog.value = it },
            title = "Worlds",
            values = worlds,
            labels = worlds.map { world -> world.name },
            selected = selected,
            onStateChanged = { world ->
                selectedId = world.id

                // MUST not use remembered scope since it will be cancelled due to dialog closing.
                CoroutineScope(Dispatchers.IO).launch {
                    refreshData(world.id)
                }
            }
        )
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