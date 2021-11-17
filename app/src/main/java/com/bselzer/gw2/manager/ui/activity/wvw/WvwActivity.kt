package com.bselzer.gw2.manager.ui.activity.wvw

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
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
import com.bselzer.gw2.manager.companion.AppCompanion
import com.bselzer.gw2.manager.companion.preference.WvwPreferenceCompanion.REFRESH_INTERVAL
import com.bselzer.gw2.manager.companion.preference.WvwPreferenceCompanion.SELECTED_WORLD
import com.bselzer.gw2.manager.configuration.wvw.WvwUpgradeProgression
import com.bselzer.gw2.manager.ui.coil.HexColorTransformation
import com.bselzer.gw2.manager.ui.theme.AppTheme
import com.bselzer.library.gw2.v2.cache.instance.ContinentCache
import com.bselzer.library.gw2.v2.cache.instance.WorldCache
import com.bselzer.library.gw2.v2.cache.instance.WvwCache
import com.bselzer.library.gw2.v2.cache.provider.Gw2CacheProvider
import com.bselzer.library.gw2.v2.model.continent.Continent
import com.bselzer.library.gw2.v2.model.continent.ContinentFloor
import com.bselzer.library.gw2.v2.model.enumeration.extension.wvw.owner
import com.bselzer.library.gw2.v2.model.enumeration.extension.wvw.type
import com.bselzer.library.gw2.v2.model.enumeration.wvw.MapBonusType
import com.bselzer.library.gw2.v2.model.enumeration.wvw.MapType
import com.bselzer.library.gw2.v2.model.enumeration.wvw.ObjectiveOwner
import com.bselzer.library.gw2.v2.model.enumeration.wvw.ObjectiveType
import com.bselzer.library.gw2.v2.model.extension.wvw.objective
import com.bselzer.library.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.library.gw2.v2.model.world.World
import com.bselzer.library.gw2.v2.model.wvw.match.WvwMapObjective
import com.bselzer.library.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.library.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.library.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.library.gw2.v2.tile.cache.metadata.id
import com.bselzer.library.gw2.v2.tile.extension.scale
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
import kotlinx.datetime.*
import org.kodein.db.Value
import timber.log.Timber
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class WvwActivity : AppCompatActivity() {
    private val config = AppCompanion.CONFIG.wvw
    private val jobs: ArrayDeque<Job> = ArrayDeque()
    private val worlds = mutableStateOf<Collection<World>>(emptyList())
    private val match = mutableStateOf<WvwMatch?>(null)
    private val objectives = mutableStateOf<Collection<WvwObjective>>(emptyList())
    private val upgrades = mutableStateOf(emptyMap<Int, WvwUpgrade>())
    private val guildUpgrades = mutableStateOf(emptyMap<Int, GuildUpgrade>())
    private val continent = mutableStateOf<Continent?>(null)
    private val floor = mutableStateOf<ContinentFloor?>(null)
    private val grid = mutableStateOf(TileGrid())
    private val zoom = config.map.defaultZoom
    private val selectedObjective = mutableStateOf<WvwObjective?>(null)
    private val tileContent = mutableStateMapOf<Value, Bitmap>()

    // TODO partial grid rending
    // TODO investigate (initial) tile download time
    // TODO mutable zoom
    // TODO match details: scores, ppt, etc
    // TODO spawn/bloodlust icons: partial color change
    // TODO DB clearing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Content() }
    }

    @OptIn(ExperimentalTime::class)
    override fun onResume() {
        super.onResume()

        CoroutineScope(Dispatchers.IO).launch {
            val interval = AppCompanion.DATASTORE.safeLatest(REFRESH_INTERVAL, 5)
            repeat(Duration.minutes(interval)) {
                refreshData()
            }
        }.addTo(jobs)
    }

    override fun onPause() {
        super.onPause()
        jobs.cancel()
    }

    /**
     * Refreshes the WvW data.
     */
    private suspend fun refreshData() {
        Timber.d("Refreshing WvW data.")

        val selectedWorld = AppCompanion.DATASTORE.nullLatest(SELECTED_WORLD)
        AppCompanion.GW2_CACHE.lockedTransaction {
            worlds.value = get<WorldCache>().findWorlds()

            // Need the world to be able to get the associated match.
            if (selectedWorld == null) {
                // Selection is required so do not allow cancellation.
                showSelectWorldDialog(cancellable = false)

                // Use the config ids to try to populate the map/grid data before the selection is made.
                refreshMapData(this)
                refreshGridData(this)
                return@lockedTransaction
            }

            val cache = get<WvwCache>()
            val match = AppCompanion.GW2.wvw.match(selectedWorld)

            // TODO Need to end the transaction so that the initial api call gets committed.
            transaction { cache.putMatch(match) }
            val objectives = cache.findObjectives(match)

            this@WvwActivity.match.value = match
            this@WvwActivity.objectives.value = objectives
            this@WvwActivity.upgrades.value = cache.findUpgrades(objectives).associateBy { it.id }
            refreshMapData(this, match)
            refreshGridData(this)
        }
    }

    /**
     * Refreshes the WvW map data using the configuration ids.
     */
    private suspend fun refreshMapData(cacheProvider: Gw2CacheProvider) = cacheProvider.apply {
        // This data should not be changing so only initialize it.
        if (continent.value != null) {
            return@apply
        }

        Timber.d("Refreshing WvW map data.")

        // Assume that all WvW maps are within the same continent and floor.
        val cache = get<ContinentCache>()
        val continent = cache.getContinent(config.map.continentId)
        this@WvwActivity.floor.value = cache.getContinentFloor(config.map.continentId, config.map.floorId)
        this@WvwActivity.continent.value = continent
    }

    /**
     * Refreshes the WvW map data using a map found from the match.
     */
    private suspend fun refreshMapData(cacheProvider: Gw2CacheProvider, match: WvwMatch) = cacheProvider.apply {
        // This data should not be changing so only initialize it.
        if (continent.value != null) {
            return@apply
        }

        Timber.d("Refreshing WvW map data.")

        // Assume that all WvW maps are within the same continent and floor.
        val mapId = match.maps.firstOrNull()?.id ?: return@apply
        val cache = get<ContinentCache>()
        val map = cache.getMap(mapId)
        val continent = cache.getContinent(map)
        this@WvwActivity.floor.value = cache.getContinentFloor(map)
        this@WvwActivity.continent.value = continent
    }

    /**
     * Refreshes the WvW map tiling grid.
     */
    private suspend fun refreshGridData(cacheProvider: Gw2CacheProvider) = cacheProvider.apply {
        val continent = continent.value
        val floor = floor.value

        // Verify that the related data exists.
        if (continent == null || floor == null) {
            return@apply
        }

        val zoom = zoom
        Timber.d("Refreshing WvW tile grid data for zoom level $zoom.")

        val gridRequest = AppCompanion.TILE.requestGrid(continent, floor, zoom).let { request ->
            if (config.map.isBounded) {
                // Cut off unneeded tiles.
                val bound = config.map.levels.firstOrNull { level -> level.zoom == zoom }?.bound
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

        for (tileRequest in gridRequest.tileRequests) {
            // Get the tile content and update the state.
            val tile = AppCompanion.TILE_CACHE.getTile(tileRequest)
            val bitmap = BitmapFactory.decodeByteArray(tile.content, 0, tile.content.size)
            tileContent[tile.id()] = bitmap
        }
    }

    @Preview
    @Composable
    private fun Content() = AppTheme {
        Column {
            Toolbar()

            val grid = remember { grid }.value

            // Until a selection is made so that tiling can be done, display a progress bar.
            // TODO transition between missing vs shown
            if (grid.tiles.isEmpty()) {
                ShowMissingGridData()
            } else {
                Box(
                    contentAlignment = Alignment.BottomStart
                ) {
                    ShowGridData()

                    // Overlay the selected objective over everything else on the map.
                    ShowSelectedObjective()
                }
            }
        }
    }

    /**
     * Displays content related to the grid data not being populated.
     */
    @Composable
    private fun ShowMissingGridData() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Background()

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Loading the WvW map.", fontWeight = FontWeight.Bold)
                CircularProgressIndicator()
            }
        }

        // Attempt to rectify the missing data.
        SideEffect {
            CoroutineScope(Dispatchers.IO).launch {
                AppCompanion.GW2_CACHE.lockedTransaction {
                    refreshGridData(AppCompanion.GW2_CACHE)
                }
            }
        }
    }

    @Composable
    private fun Background() = Image(
        painter = painterResource(id = R.drawable.gw2_ice),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    /**
     * Displays the grid content.
     */
    @Composable
    private fun ShowGridData() {
        val horizontal = rememberScrollState()
        val vertical = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontal)
                .verticalScroll(vertical)
        ) {
            ShowMap()
            ShowObjectives()

            if (config.bloodlust.enabled) {
                ShowBloodlust()
            }
        }

        if (config.map.scroll.enabled) {
            InitialMapScroll(horizontal, vertical)
        }
    }

    @Composable
    private fun InitialMapScroll(horizontal: ScrollState, vertical: ScrollState) {
        // Can't scale without knowing the continent dimensions and floor regions/maps.
        val floor = remember { floor }.value
        val continent = remember { continent }.value
        val grid = remember { grid }.value
        if (continent != null && floor != null && grid.tiles.isNotEmpty()) {
            // Get the WvW region. It should be the only one that exists within this floor.
            val region = floor.regions.values.firstOrNull { region -> region.name == config.map.regionName }

            // Scroll over to the configured map.
            region?.maps?.values?.firstOrNull { map -> map.name == config.map.scroll.mapName }?.let { eb ->
                val topLeft = eb.continentRectangle.point1.scale(grid, continent, zoom)
                rememberCoroutineScope().launch {
                    horizontal.animateScrollTo(topLeft.x.toInt())
                    vertical.animateScrollTo(topLeft.y.toInt())
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
        val density = LocalDensity.current
        val grid = remember { grid }.value
        val tileContent = remember { tileContent }
        for (row in grid.grid) {
            Row {
                for (tile in row) {
                    // Need to specify non-zero width/height on the default bitmap.
                    val bitmap = tileContent[tile.id()] ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
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
            }
        }
    }

    /**
     * Displays the objectives on the map.
     */
    @OptIn(ExperimentalFoundationApi::class, ExperimentalTime::class)
    @Composable
    private fun ShowObjectives() {
        val match = remember { match }.value
        remember { objectives }.value.forEach { objective ->
            // Find the objective through the match in order to find out who the owner is.
            val matchObjective = match.objective(objective) ?: return@forEach
            val owner = matchObjective.owner() ?: ObjectiveOwner.NEUTRAL

            val configObjective = configObjective(objective)
            val coordinates = scaledCoordinates(objective) ?: return
            val size = objectiveSize(objective)

            // Use a default link when the icon link doesn't exist. The link won't exist for atypical types such as Spawn/Mercenary.
            val link = if (objective.iconLink.isNotBlank()) objective.iconLink else configObjective?.defaultIconLink
            val request = ImageRequest.Builder(LocalContext.current)
                .data(link)
                .size(size.width.toInt(), size.height.toInt())
                .transformations(OwnedColorTransformation(config, owner))
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
                    painter = rememberImagePainter(request, AppCompanion.IMAGE_LOADER),
                    contentDescription = objective.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .constrainAs(icon) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                        .size(widthDp, heightDp)
                        .combinedClickable(onLongClick = {
                            //TODO show specifics: claimed by, upgrades/tactics/improvements, etc
                        }) {
                            selectedObjective.value = objective
                        }
                )

                // Need to do the constraining within the scope of the ConstraintLayout.
                if (config.objectives.progressions.enabled) {
                    val progression = getProgression(objective.upgradeId, matchObjective.yaksDelivered)
                    progression?.iconLink?.let { iconLink ->
                        val upgradeSize = progression.size ?: config.objectives.progressions.defaultSize
                        ShowIndicator(iconLink, upgradeSize, "Upgraded", Modifier.constrainAs(upgradeIndicator) {
                            // Display the indicator in the top center of the objective icon.
                            top.linkTo(icon.top)
                            start.linkTo(icon.start)
                            end.linkTo(icon.end)
                        })
                    }
                }

                if (config.objectives.claim.enabled && !matchObjective.claimedBy.isNullOrBlank()) {
                    config.objectives.claim.iconLink?.let { iconLink ->
                        ShowIndicator(iconLink, config.objectives.claim.size, "Guild Claimed", Modifier.constrainAs(claimIndicator) {
                            // Display the indicator in the bottom right of the objective icon.
                            bottom.linkTo(icon.bottom)
                            end.linkTo(icon.end)
                        })
                    }
                }

                if (config.objectives.waypoint.enabled) {
                    ShowWaypointIndicator(objective, matchObjective, Modifier.constrainAs(waypointIndicator) {
                        // Display the indicator in the bottom left of the objective icon.
                        bottom.linkTo(icon.bottom)
                        start.linkTo(icon.start)
                    })
                }

                if (config.objectives.immunity.enabled) {
                    val immunity = configObjective?.immunity ?: config.objectives.immunity.defaultDuration
                    val flippedAt = matchObjective.lastFlippedAt
                    if (immunity != null && flippedAt != null) {
                        // Display the timer underneath the objective icon.
                        ShowImmunityTimer(immunity, flippedAt, Modifier.constrainAs(timer) {
                            top.linkTo(icon.bottom)
                            start.linkTo(icon.start)
                            end.linkTo(icon.end)
                        })
                    }
                }
            }
        }
    }

    /**
     * @return the progression level associated with the upgrade associated with the objective
     */
    @Composable
    private fun getProgression(upgradeId: Int, yaksDelivered: Int): WvwUpgradeProgression? {
        val upgrades = remember { upgrades }.value
        val upgrade = upgrades[upgradeId] ?: return null
        val level = upgrade.tiers.count { tier -> yaksDelivered >= tier.yaksRequired }
        return config.objectives.progressions.progression.getOrNull(level - 1)
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
            painter = rememberImagePainter(request, AppCompanion.IMAGE_LOADER),
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
        val waypoint = config.objectives.waypoint
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
            transformations.add(HexColorTransformation(config.objectives.waypoint.guild.color))
        }

        ShowIndicator(iconLink, size = config.objectives.waypoint.size, contentDescription = "Waypoint", modifier = modifier, transformations = transformations)
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
            fontSize = config.objectives.immunity.textSize.sp,
            color = androidx.compose.ui.graphics.Color.White,
            modifier = modifier.wrapContentSize()
        )

        LaunchedEffect(key1 = countdown) {
            // Advance the countdown.
            delay(Duration.milliseconds(config.objectives.immunity.delay))
            countdown += 1
        }
    }

    /**
     * Displays general information about the objective the user clicked on in a pop-up label.
     */
    @Composable
    private fun ShowSelectedObjective() {
        val selected = config.objectives.selected
        val selectedObjective = remember { selectedObjective }.value ?: return
        val match = remember { match }.value
        val matchObjective = match.objective(selectedObjective)
        val owner = matchObjective?.owner() ?: ObjectiveOwner.NEUTRAL
        val title = "${selectedObjective.name} (${owner.userFriendly()} ${selectedObjective.type})"

        Box(
            modifier = Modifier.wrapContentSize()
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
        val bloodlust = config.bloodlust
        val match = remember { match }.value ?: return
        val continent = remember { continent }.value ?: return
        val objectives = remember { objectives }.value

        val width = bloodlust.size.width
        val height = bloodlust.size.height

        val borderlands = match.maps.filter { map -> map.type().isOneOf(MapType.BLUE_BORDERLANDS, MapType.RED_BORDERLANDS, MapType.GREEN_BORDERLANDS) } ?: return
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
                .transformations(OwnedColorTransformation(config, owner))
                .build()

            // Scale the position before using it.
            val x = objectiveRuins.sumOf { ruin -> ruin.coordinates.x } / objectiveRuins.count()
            val y = objectiveRuins.sumOf { ruin -> ruin.coordinates.y } / objectiveRuins.count()
            val coordinates = Point2D(x, y).scaledCoordinates(this.grid.value, continent, zoom, Dimension2D(width.toDouble(), height.toDouble()))

            // Measurements are done with DP so conversion must be done from pixels.
            val density = LocalDensity.current
            val xDp = density.run { coordinates.x.toInt().toDp() }
            val yDp = density.run { coordinates.y.toInt().toDp() }
            val widthDp = density.run { width.toDp() }
            val heightDp = density.run { height.toDp() }

            Image(
                painter = rememberImagePainter(request, AppCompanion.IMAGE_LOADER),
                contentDescription = "Bloodlust",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .absoluteOffset(xDp, yDp)
                    .size(widthDp, heightDp)
            )
        }
    }

    /**
     * @return the objective from the configuration that matches the objective from the objectives endpoint
     */
    private fun configObjective(objective: WvwObjective): com.bselzer.gw2.manager.configuration.wvw.WvwObjective? {
        val type = objective.type()
        return config.objectives.objectives.firstOrNull { configObjective -> configObjective.type == type }
    }

    /**
     * @return the size of the image associated with an objective
     */
    private fun objectiveSize(objective: WvwObjective): Dimension2D {
        val configObjective = configObjective(objective)

        // Get the size from the configured objective if it is defined, otherwise use the default.
        val width = configObjective?.size?.width ?: config.objectives.defaultSize.width
        val height = configObjective?.size?.height ?: config.objectives.defaultSize.height
        return Dimension2D(width.toDouble(), height.toDouble())
    }

    /**
     * @return the scaled coordinates of the image associated with an objective
     */
    private fun scaledCoordinates(objective: WvwObjective): Point2D? {
        val continent = this.continent.value ?: return null

        // Use the explicit coordinates if they exist, otherwise default to the label coordinates. This is needed for atypical types such as Spawn/Mercenary.
        val coordinates = if (objective.coordinates.x != 0.0 && objective.coordinates.y != 0.0) Point2D(objective.coordinates.x, objective.coordinates.y) else objective.labelCoordinates

        // Scale the objective coordinates to the zoom level and remove excluded bounds.
        return coordinates.scaledCoordinates(this.grid.value, continent, zoom, objectiveSize(objective))
    }

    /**
     * @return the scaled coordinates of the image
     */
    private fun Point2D.scaledCoordinates(grid: TileGrid, continent: Continent, zoom: Int, size: Dimension2D): Point2D =
        // Scale the objective coordinates to the zoom level and remove excluded bounds.
        scale(grid, continent, zoom).run {
            // Displace the coordinates so that it aligns with the center of the image.
            copy(x = x - size.width / 2, y = y - size.height / 2)
        }

    @Composable
    private fun Toolbar() = TopAppBar(
        title = { Text(text = stringResource(id = R.string.activity_wvw), fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = { finish() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { showSelectWorldDialog() }) {
                Icon(Icons.Filled.List, contentDescription = "World")
            }
            IconButton(onClick = { CoroutineScope(Dispatchers.IO).launch { refreshData() } }) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
            }
        }
    )

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
            val selectedId = AppCompanion.DATASTORE.nullLatest(SELECTED_WORLD)
            Timber.d("Selected world id: $selectedId")

            // If there is no matching world then the resulting -1 will specify no selection.
            val selectedWorld = worlds.indexOfFirst { world -> world.id == selectedId }
            withContext(Dispatchers.Main)
            {
                AlertDialog.Builder(this@WvwActivity)
                    .setTitle("Worlds")
                    .setSingleChoiceItems(worlds.map { world -> world.name }.toTypedArray(), selectedWorld) { dialog, which ->
                        CoroutineScope(Dispatchers.IO).launch {
                            AppCompanion.DATASTORE.update(SELECTED_WORLD, worlds[which].id)
                            refreshData()
                        }
                        dialog.dismiss()
                    }
                    .setCancelable(cancellable)
                    .show()
            }
        }
    }
}