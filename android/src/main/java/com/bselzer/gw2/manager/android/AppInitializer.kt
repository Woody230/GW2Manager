package com.bselzer.gw2.manager.android

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * The application used for initialization.
 */
class AppInitializer : Application() {
    /**
     * The default preferences datastore.
     */
    val Context.datastore: DataStore<Preferences> by preferencesDataStore("default")
}