package com.bselzer.gw2.manager.ui.activity.wvw

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.companion.AppCompanion
import com.bselzer.gw2.manager.companion.preference.WvwPreferenceCompanion.REFRESH_INTERVAL
import com.bselzer.gw2.manager.companion.preference.WvwPreferenceCompanion.SELECTED_WORLD
import com.bselzer.gw2.manager.ui.theme.AppTheme
import com.bselzer.library.gw2.v2.model.enumeration.wvw.MapType
import com.bselzer.library.gw2.v2.model.world.World
import com.bselzer.library.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.library.gw2.v2.model.wvw.objective.WvwObjective
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
    private val matches = mutableStateOf(emptyList<WvwMatch>())
    private val objectives = mutableStateOf(emptyList<WvwObjective>())

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
        val wvw = AppCompanion.GW2.wvw
        matches.value = wvw.matches() // TODO is this needed or can other endpoint(s) be used to reduce data
        objectives.value = wvw.objectives()

        // TODO other data
    }

    @Preview
    @Composable
    private fun Content() = AppTheme {
        // Focus the initial scroll position on the Eternal Battlegrounds.
        val component = MapComponent(MapType.ETERNAL_BATTLEGROUNDS)
        val initialHorizontal = component.position.x
        val initialVertical = component.position.y

        // TODO zooming in and out
        // TODO mapping objective id (excluding realm prefix and dash) to location
        Image(
            painter = painterResource(id = R.drawable.gw2_wvw_map),
            contentDescription = "WvW Map",
            contentScale = ContentScale.None,
            modifier = Modifier
                .horizontalScroll(rememberScrollState(initialHorizontal))
                .verticalScroll(rememberScrollState(initialVertical))
        )
    }

    @Composable
    private fun Toolbar() {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.activity_wvw), fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { finish() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { selectWorld() }) {
                    Icon(Icons.Filled.List, contentDescription = "World")
                }
                IconButton(onClick = { CoroutineScope(Dispatchers.IO).launch { refreshData() } }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                }
            }
        )
    }

    /**
     * Create a dialog for the user to select the world.
     */
    private fun selectWorld() {
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
                    .show()
            }
        }
    }
}