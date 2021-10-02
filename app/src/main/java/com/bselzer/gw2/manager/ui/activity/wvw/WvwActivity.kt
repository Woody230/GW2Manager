package com.bselzer.gw2.manager.ui.activity.wvw

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.companion.AppCompanion
import com.bselzer.gw2.manager.companion.preference.WvwPreferenceCompanion
import com.bselzer.gw2.manager.ui.theme.AppTheme
import com.bselzer.library.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.library.kotlin.extension.coroutine.cancel
import com.bselzer.library.kotlin.extension.coroutine.repeat
import com.bselzer.library.kotlin.extension.function.collection.addTo
import com.bselzer.library.kotlin.extension.preference.safeLatest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class WvwActivity : AppCompatActivity() {
    private val jobs: ArrayDeque<Job> = ArrayDeque()
    private val matches = mutableStateOf(emptyList<WvwMatch>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            supportActionBar?.let {
                it.title = stringResource(id = R.string.activity_wvw)
                it.setDisplayHomeAsUpEnabled(true)
            }
            Content()
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun onResume() {
        super.onResume()

        CoroutineScope(Dispatchers.IO).launch {
            val interval = AppCompanion.DATASTORE.safeLatest(WvwPreferenceCompanion.REFRESH_INTERVAL, 5)
            repeat(Duration.minutes(interval)) {
                Timber.d("Refreshing WvW data.")
                matches.value = AppCompanion.GW2.wvw.matches()
                // TODO other data
            }
        }.addTo(jobs)
    }

    override fun onPause() {
        super.onPause()
        jobs.cancel()
    }

    @Preview
    @Composable
    private fun Content() = AppTheme {
        // TODO layout
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}