package com.bselzer.gw2.manager.android.ui.activity.setting.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bselzer.library.kotlin.extension.compose.preference.safeRemember
import com.bselzer.library.kotlin.extension.preference.initialize
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class WvwPreference(datastore: DataStore<Preferences>) : AppPreference(datastore) {
    private companion object {
        const val base = "WVW"

        /**
         * The WvW refresh interval preference key.
         */
        val REFRESH_INTERVAL = stringPreferencesKey("$base.RefreshInterval")

        /**
         * The WvW selected world preference key.
         */
        val SELECTED_WORLD = intPreferencesKey("$base.SelectedWorld")
    }

    /**
     * The data refresh interval.
     */
    var refreshInterval: Duration
        get() = getDuration(REFRESH_INTERVAL, refreshIntervalDefault)
        set(value) = putDuration(REFRESH_INTERVAL, value)

    /**
     * The default refresh interval.
     */
    val refreshIntervalDefault: Duration = Duration.minutes(10)

    /**
     * The default refresh interval unit.
     */
    val refreshIntervalDefaultUnit: DurationUnit = DurationUnit.MINUTES

    /**
     * The id of the selected world.
     */
    var selectedWorld: Int
        get() = getInt(SELECTED_WORLD)
        set(value) = putInt(SELECTED_WORLD, value)

    /**
     * Initializes the selected world.
     */
    suspend fun initializeSelectedWorld(world: Int) = datastore.initialize(SELECTED_WORLD, world)

    /**
     * @return the state of the refresh interval
     */
    @Composable
    fun rememberRefreshInterval(): MutableState<Duration> = datastore.safeRemember(REFRESH_INTERVAL, refreshIntervalDefault)
}