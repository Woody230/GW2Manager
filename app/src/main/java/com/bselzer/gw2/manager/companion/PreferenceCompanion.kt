package com.bselzer.gw2.manager.companion

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

object PreferenceCompanion
{
    /**
     * Singleton common preferences.
     */
    val Context.DEFAULT_PREFERENCES: DataStore<Preferences> by preferencesDataStore("default")

    /**
     * The build number key.
     */
    val BUILD_NUMBER = intPreferencesKey("BuildNumber")
}