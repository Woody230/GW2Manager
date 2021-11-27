package com.bselzer.gw2.manager.android.ui.activity.setting.preference

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bselzer.gw2.manager.android.ui.theme.Theme
import com.bselzer.library.kotlin.extension.compose.preference.nullRemember
import com.bselzer.library.kotlin.extension.compose.preference.safeRemember
import com.bselzer.library.kotlin.extension.preference.initialize
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CommonPreference(datastore: DataStore<Preferences>) : AppPreference(datastore) {
    private companion object {
        const val base = "Common"

        /**
         * The build number preference key.
         */
        val BUILD_NUMBER = intPreferencesKey("$base.BuildNumber")

        /**
         * The token preference key.
         */
        val TOKEN = stringPreferencesKey("$base.Token")

        /**
         * The UI theme preference key.
         */
        val THEME = stringPreferencesKey("$base.Theme")
    }

    // TODO better way to handle prefs, particularly with the datastore extensions
    /**
     * The GW2 build version.
     */
    var buildNumber: Int
        get() = getInt(BUILD_NUMBER)
        set(value) = putInt(BUILD_NUMBER, value)

    /**
     * The GW2 API token or api key.
     */
    var token: String?
        get() = getNullString(TOKEN)
        set(value) = putString(TOKEN, value)

    /**
     * The UI theme.
     */
    var theme: Theme
        get() = getEnum(THEME, Theme.LIGHT)
        set(value) = putEnum(THEME, value)

    /**
     * Initializes the theme if it doesn't exist.
     */
    @Composable
    fun InitializeTheme() {
        // TODO add to extensions
        val theme = if (isSystemInDarkTheme()) Theme.DARK else Theme.LIGHT
        LaunchedEffect(THEME) {
            datastore.initialize(THEME, Json.encodeToString(theme))
        }
    }

    /**
     * @return the state of the UI theme
     */
    @Composable
    fun rememberTheme(): MutableState<Theme> = datastore.safeRemember(THEME, Theme.LIGHT)

    /**
     * @return the state of the token
     */
    @Composable
    fun rememberToken(): MutableState<String?> = datastore.nullRemember(TOKEN)
}