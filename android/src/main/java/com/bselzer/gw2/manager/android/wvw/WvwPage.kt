package com.bselzer.gw2.manager.android.wvw

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.bselzer.gw2.manager.android.R
import com.bselzer.gw2.manager.android.common.BasePage
import com.bselzer.gw2.manager.android.wvw.page.WvwMapPage
import com.bselzer.gw2.manager.android.wvw.page.WvwMatchPage
import com.bselzer.gw2.manager.android.wvw.page.WvwSelectedObjectivePage
import com.bselzer.gw2.manager.android.wvw.state.WvwState
import com.bselzer.gw2.manager.common.expect.Gw2Aware
import com.bselzer.gw2.v2.cache.instance.ContinentCache
import com.bselzer.gw2.v2.cache.instance.GuildCache
import com.bselzer.gw2.v2.cache.instance.WorldCache
import com.bselzer.gw2.v2.cache.instance.WvwCache
import com.bselzer.gw2.v2.model.extension.wvw.objective
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.tile.model.response.Tile
import com.bselzer.gw2.v2.tile.model.response.TileGrid
import com.bselzer.ktx.compose.effect.PreRepeatedEffect
import com.bselzer.ktx.compose.ui.appbar.MaterialAppBar
import com.bselzer.ktx.compose.ui.appbar.RefreshIcon
import com.bselzer.ktx.compose.ui.appbar.UpNavigationIcon
import com.bselzer.ktx.compose.ui.dialog.SingleChoiceDialog
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.settings.compose.safeState
import kotlinx.coroutines.*
import kotlin.time.ExperimentalTime

class WvwPage(
    aware: Gw2Aware,
    private val navigateUp: () -> Unit,
    private val backEnabled: Boolean,
    private val selectedPage: MutableState<PageType>
) : BasePage(aware) {
    private val state: WvwState = WvwState(
        configuration = configuration.wvw,
        emblemClient = emblemClient,
        zoom = mutableStateOf(value = configuration.wvw.map.zoom.default)
    )
    private val showWorldDialog: MutableState<Boolean> = mutableStateOf(false)

    enum class PageType {
        MENU, // TODO remove menu and allow for modal drawer use everywhere instead of up navigation in this page and other pages?
        MAP,
        MATCH,
        DETAILED_SELECTED_OBJECTIVE
        // TODO all matches overview (who vs who)
    }

    // region Refresh
    /**
     * Refreshes the WvW data.
     */
    private suspend fun refreshData(selectedWorld: Int) = withContext(Dispatchers.IO) {
        Logger.d("Refreshing WvW data for world ${selectedWorld}.")

        gw2Cache.lockedInstance {
            state.worlds.value = get<WorldCache>().findWorlds()

            // Need the world to be able to get the associated match.
            if (selectedWorld <= 0) {
                showWorldDialog.value = true
                return@lockedInstance
            }

            val cache = get<WvwCache>()
            val match = gw2Client.wvw.match(selectedWorld)
            cache.putMatch(match)
            val objectives = cache.findObjectives(match)

            state.match.value = match
            state.objectives.value = objectives
            state.upgrades.value = cache.findUpgrades(objectives).associateBy { it.id }
            state.guildUpgrades.value = cache.findGuildUpgrades(objectives.mapNotNull { objective -> match.objective(objective) }).associateBy { it.id }

            // Map refresh is comparatively expensive so only do it when the user is on its page.
            if (selectedPage.value == PageType.MAP) {
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
            state.floor.value = cache.getContinentFloor(configuration.wvw.map.continentId, configuration.wvw.map.floorId)
            state.continent.value = continent
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
            state.floor.value = cache.getContinentFloor(map)
            state.continent.value = continent
        }
    }

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

            val zoom = state.zoom.value
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
     * Refreshes the guild data for the [id].
     */
    private suspend fun refreshGuild(id: String?) = withContext(Dispatchers.IO) {
        if (id.isNullOrBlank()) {
            // Skip refreshing bad ids.
            return@withContext
        }

        state.guilds[id] = gw2Cache.instance {
            get<GuildCache>().getGuild(id)
        }
    }

    // endregion Refresh

    // region Menu

    @OptIn(ExperimentalTime::class)
    @Composable
    override fun Content() {
        var selectedPage by rememberSaveable { selectedPage }

        Logger.d("Displaying World vs. World page $selectedPage")
        when (selectedPage) {
            PageType.MAP -> {
                WvwMapPage(
                    aware = this,
                    navigateUp = navigateUp,
                    appBarActions = commonAppBarActions(),
                    state = state.rememberMap(),
                    setPage = { selectedPage = it }
                ).Content()
            }
            PageType.MATCH -> {
                WvwMatchPage(
                    aware = this,
                    navigateUp = navigateUp,
                    appBarActions = commonAppBarActions(),
                    state = state.rememberMatch(),
                ).Content()
            }
            PageType.DETAILED_SELECTED_OBJECTIVE -> {
                WvwSelectedObjectivePage(
                    aware = this,
                    navigateUp = navigateUp,
                    appBarActions = commonAppBarActions(),
                    state = state.rememberSelected(),
                ).Content()

                remember { state.selectedObjective }.value?.let { objective ->
                    remember { state.match }.value.objective(objective)?.let { matchObjective ->
                        LaunchedEffect(matchObjective.claimedBy) {
                            refreshGuild(matchObjective.claimedBy)
                        }
                    }
                }
            }
            PageType.MENU -> Menu()
        }

        if (remember { showWorldDialog }.value) {
            SelectWorldDialog()
        }

        // Map refresh is comparatively expensive so only do it when the user is on its page.
        LaunchedEffect(selectedPage) {
            if (selectedPage == PageType.MAP) {
                refreshMapData()
                refreshGridData()
            }
        }

        // TODO maintain last refresh time instead
        PreRepeatedEffect(delay = runBlocking { wvwPref.refreshInterval.get() }) {
            refreshData(wvwPref.selectedWorld.get())
        }

        LaunchedEffect(key1 = state.zoom.value) {
            refreshGridData()
        }

        BackHandler(enabled = backEnabled) {
            when (selectedPage) {
                PageType.MAP -> selectedPage = PageType.MENU
                PageType.MATCH -> selectedPage = PageType.MENU
                PageType.DETAILED_SELECTED_OBJECTIVE -> {
                    // Go back to the map page with the objective cleared so that the pop-up is not displayed.
                    selectedPage = PageType.MAP
                    state.selectedObjective.value = null
                }
                PageType.MENU -> navigateUp()
            }
        }
    }

    /**
     * Lays out the World vs. World menu.
     */
    @Composable
    private fun Menu() = Column {
        // Match the same way that the MainActivity is displayed.
        MaterialAppBar(title = stringResource(R.string.activity_wvw), navigationIcon = { UpNavigationIcon(onClick = navigateUp) })
        AbsoluteBackground {
            ShowMenu(
                stringResource(id = R.string.wvw_map) to { selectedPage.value = PageType.MAP },
                stringResource(id = R.string.wvw_match) to { selectedPage.value = PageType.MATCH }
            )
        }
    }

    // endregion Menu

    // region Common

    /**
     * Lays out the common icons for the pages of this activity.
     */
    @Composable
    private fun commonAppBarActions(): @Composable RowScope.() -> Unit = {
        val selectedId by wvwPref.selectedWorld.safeState()
        RefreshIcon { refreshData(selectedId) }
        IconButton(onClick = { showWorldDialog.value = true }) {
            Icon(Icons.Filled.List, contentDescription = "World")
        }
    }

    /**
     * Create a dialog for the user to select the world.
     */
    @Composable
    private fun SelectWorldDialog() {
        val worlds = remember { state.worlds }.value.sortedBy { world -> world.name }
        if (worlds.isEmpty()) {
            Toast.makeText(LocalContext.current, "Waiting for the worlds to be downloaded.", Toast.LENGTH_SHORT).show()
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
}