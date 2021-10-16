package com.bselzer.gw2.manager.ui.activity.wvw

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.companion.AppCompanion
import com.bselzer.gw2.manager.companion.preference.WvwPreferenceCompanion.REFRESH_INTERVAL
import com.bselzer.gw2.manager.companion.preference.WvwPreferenceCompanion.SELECTED_WORLD
import com.bselzer.gw2.manager.ui.theme.AppTheme
import com.bselzer.library.gw2.v2.model.continent.Continent
import com.bselzer.library.gw2.v2.model.continent.ContinentFloor
import com.bselzer.library.gw2.v2.model.enumeration.extension.wvw.mapType
import com.bselzer.library.gw2.v2.model.enumeration.extension.wvw.owner
import com.bselzer.library.gw2.v2.model.enumeration.wvw.ObjectiveOwner
import com.bselzer.library.gw2.v2.model.extension.wvw.objectiveId
import com.bselzer.library.gw2.v2.model.world.World
import com.bselzer.library.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.library.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.library.gw2.v2.tile.model.response.TileGrid
import com.bselzer.library.kotlin.extension.coroutine.cancel
import com.bselzer.library.kotlin.extension.coroutine.repeat
import com.bselzer.library.kotlin.extension.function.collection.addTo
import com.bselzer.library.kotlin.extension.preference.nullLatest
import com.bselzer.library.kotlin.extension.preference.safeLatest
import com.bselzer.library.kotlin.extension.preference.update
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class WvwActivity : AppCompatActivity() {
    private val jobs: ArrayDeque<Job> = ArrayDeque()
    private val worlds = mutableStateOf(emptyList<World>())
    private val match = mutableStateOf<WvwMatch?>(null)
    private val objectives = mutableStateOf(emptyList<WvwObjective>())
    private val continent = mutableStateOf<Continent?>(null)
    private val floor = mutableStateOf<ContinentFloor?>(null)
    private val grid = mutableStateOf<TileGrid?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Toolbar()
                Content()
            }
        }
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

        // Set up data that should not be changing.
        if (worlds.value.isEmpty()) {
            worlds.value = AppCompanion.GW2.world.worlds()
        }

        // Set up or update data that will change.
        val selectedWorld = AppCompanion.DATASTORE.nullLatest(SELECTED_WORLD)
        if (selectedWorld == null) {
            // Selection is required so do not allow cancellation.
            showSelectWorldDialog(cancellable = false)
        } else {
            val wvw = AppCompanion.GW2.wvw
            val match = wvw.match(selectedWorld)
            val objectives = wvw.objectives(match.maps.flatMap { map -> map.objectives.map { objective -> objective.id } })
            this.match.value = match
            this.objectives.value = objectives
            refreshMapData(match)
            refreshGridData()
        }
    }

    /**
     * Refreshes the WvW map data.
     */
    private suspend fun refreshMapData(match: WvwMatch) {
        // This data should not be changing so only initialize it.
        if (continent.value != null) {
            return
        }

        Timber.d("Refreshing WvW map data.")

        // Assume that all WvW maps are within the same continent and floor.
        val mapId = match.maps.firstOrNull()?.id ?: return
        val map = AppCompanion.GW2.map.map(mapId)
        val continent = AppCompanion.GW2.continent.continent(map.continentId)
        floor.value = AppCompanion.GW2.continent.floor(map.continentId, map.defaultFloorId)
        this.continent.value = continent
    }

    /**
     * Refreshes the WvW map tiling grid.
     */
    private suspend fun refreshGridData() {
        val grid = this.grid.value

        // Verify the grid does not exist yet.
        if (grid == null || grid.tiles.isEmpty()) {
            val continent = this.continent.value
            val floor = this.floor.value

            // Verify that the related data exists.
            if (continent != null && floor != null) {
                // TODO configurable zoom
                Timber.d("Refreshing WvW tile grid data.")
                this.grid.value = AppCompanion.TILE.grid(continent, floor, 5)
            }
        }
    }

    @Preview
    @Composable
    private fun Content() = AppTheme {
        val grid = remember { grid }.value

        // Until a selection is made so that tiling can be done, display a progress bar.
        // TODO transition between missing vs shown
        // TODO partial loading as tiles get downloaded or retrieved from cache
        if (grid == null || grid.tiles.isEmpty()) {
            ShowMissingGridData()
        } else {
            ShowGridData(grid)
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
                refreshGridData()
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
    private fun ShowGridData(grid: TileGrid) {
        // TODO Focus the initial scroll position on the Eternal Battlegrounds.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(rememberScrollState())
                .verticalScroll(rememberScrollState())
        ) {
            ShowMap(grid)
            ShowObjectives()
        }
    }

    /**
     * Displays the map represented by a tiled grid.
     */
    @Composable
    private fun ShowMap(grid: TileGrid) = Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val density = LocalDensity.current

        Timber.d("Displaying grid with tile size ${grid.tileWidth} x ${grid.tileHeight}.")
        for (row in grid.grid) {
            Row {
                for (tile in row) {
                    // TODO use coil to cache bitmaps
                    val bitmap = BitmapFactory.decodeByteArray(tile.content, 0, tile.content.size) ?: continue
                    Image(
                        painter = BitmapPainter(bitmap.asImageBitmap()),
                        contentDescription = "WvW Map",
                        modifier = Modifier.size(density.run { grid.tileWidth.toDp() }, density.run { grid.tileHeight.toDp() })
                    )
                }
            }
        }
    }

    /**
     * Displays the objectives on the map.
     */
    @Composable
    private fun ShowObjectives() = remember { objectives }.value.forEach { objective ->
        val density = LocalDensity.current

        // Find the objective through the match in order to find out who the owner is.
        val match = match.value?.maps?.firstOrNull { map -> map.id == objective.mapId }?.objectives?.firstOrNull { match -> match.id == objective.id } ?: return@forEach
        val owner = match.owner() ?: ObjectiveOwner.NEUTRAL

        // Find the objective through the config in order to get info related to displaying the image.
        val mapType = objective.mapType() ?: return@forEach
        val config = AppCompanion.CONFIG.wvw.map(mapType).objectives.firstOrNull { config -> config.id == objective.objectiveId() } ?: return@forEach

        // Overlay the objective image onto the map image.
        Image(
            painter = config.ownedPainter(owner, resources),
            contentDescription = objective.name,
            contentScale = ContentScale.None,

            // Offset needs to be done with DP so conversion must be done from pixels.
            // TODO set actual objective positions
            modifier = Modifier.absoluteOffset(density.run { 0.toDp() }, density.run { 0.toDp() })
        )
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
        val worlds = this.worlds.value.sortedBy { world -> world.name }
        if (worlds.isEmpty()) {
            Toast.makeText(this, "Awaiting the download of worlds.", Toast.LENGTH_SHORT).show()
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
                        AppCompanion.DATASTORE.update(SELECTED_WORLD, worlds[which].id, CoroutineScope(Dispatchers.IO))
                        dialog.dismiss()
                    }
                    .setCancelable(cancellable)
                    .show()
            }
        }
    }
}