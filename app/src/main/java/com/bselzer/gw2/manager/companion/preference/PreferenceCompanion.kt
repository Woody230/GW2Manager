package com.bselzer.gw2.manager.companion.preference

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceCompanion {
    /**
     * The build number preference key.
     */
    val BUILD_NUMBER = intPreferencesKey("BuildNumber")

    /**
     * The token preference key.
     */
    val TOKEN = stringPreferencesKey("Token")

    /**
     * The UI theme preference key.
     */
    val THEME = stringPreferencesKey("Theme")
}