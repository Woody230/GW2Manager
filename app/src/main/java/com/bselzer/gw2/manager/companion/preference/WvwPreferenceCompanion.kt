package com.bselzer.gw2.manager.companion.preference

import androidx.datastore.preferences.core.intPreferencesKey

object WvwPreferenceCompanion {
    private const val WVW = "WVW"

    /**
     * The WvW refresh interval preference key.
     */
    val REFRESH_INTERVAL = intPreferencesKey("$WVW.RefreshInterval")
}