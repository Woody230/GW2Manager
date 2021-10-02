package com.bselzer.gw2.manager.companion.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

object PreferenceCompanion {
    /**
     * Singleton common preferences.
     */
    val Context.DATASTORE: DataStore<Preferences> by preferencesDataStore("default")

    /**
     * The build number preference key.
     */
    val BUILD_NUMBER = intPreferencesKey("BuildNumber")

    /**
     * The token preference key.
     */
    val TOKEN = stringPreferencesKey("Token")
}