package com.bselzer.gw2.manager.ui.activity.setting

import android.os.Bundle
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.preference.*
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.companion.preference.PreferenceCompanion
import com.bselzer.gw2.manager.companion.preference.WvwPreferenceCompanion.SELECTED_WORLD
import com.bselzer.gw2.manager.ui.kodein.DIAwarePreferenceFragment
import com.bselzer.gw2.manager.ui.theme.Theme
import com.bselzer.library.gw2.v2.model.account.token.TokenInfo
import com.bselzer.library.gw2.v2.model.enumeration.extension.account.permissions
import com.bselzer.library.gw2.v2.scope.core.Permission
import com.bselzer.library.kotlin.extension.coroutine.showToast
import com.bselzer.library.kotlin.extension.function.objects.userFriendly
import com.bselzer.library.kotlin.extension.preference.*
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import kotlin.time.ExperimentalTime

class CommonSettingsFragment : DIAwarePreferenceFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = DataStoreWrapper(datastore)
        preferenceScreen = preferenceManager.createPreferenceScreen(context).apply {
            tokenPreference(datastore).addTo(this)
            themePreference(datastore).addTo(this)
        }
    }

    /**
     * Creates the token preference.
     */
    private fun tokenPreference(datastore: DataStore<Preferences>) = EditTextPreference(context).apply {
        key = PreferenceCompanion.TOKEN.name
        summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        title = "Token"
        setIcon(R.drawable.gw2_black_lion_key)
        dialogTitle = "Token"
        setDialogIcon(R.drawable.gw2_black_lion_key)
        onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            CoroutineScope(Dispatchers.Main).launch {
                // Validate the new token before committing it.
                val token = newValue?.toString()?.trim() ?: ""
                if (token.isBlank()) {
                    datastore.clear(PreferenceCompanion.TOKEN, this)
                    text = token
                    return@launch
                }

                try {
                    withContext(Dispatchers.IO) {
                        val tokenInfo = gw2Client.token.information(token = token)
                        initializePreferences(tokenInfo)
                        datastore.update(PreferenceCompanion.TOKEN, token)
                        gw2Client.config { copy(token = token) }
                        Timber.d("Set client token to $token")
                    }

                    text = token
                } catch (ex: ClientRequestException) {
                    showToast(context, "Unable to save the token: ${ex.response.readText()}", Toast.LENGTH_LONG)
                } catch (ex: Exception) {
                    showToast(context, "Unable to save the token.", Toast.LENGTH_SHORT)
                }
            }
            return@OnPreferenceChangeListener false
        }
    }

    /**
     * Set up other preferences based on the [tokenInfo].
     */
    @OptIn(ExperimentalTime::class)
    private suspend fun initializePreferences(tokenInfo: TokenInfo) {
        try {
            val permissions = tokenInfo.permissions()
            Timber.d("Token permissions: $permissions")

            val token = tokenInfo.id

            // TODO scope processor to automatically verify permissions
            if (permissions.contains(Permission.ACCOUNT)) {
                val account = gw2Client.account.account(token)
                datastore.initialize(SELECTED_WORLD, account.world)
            }
        } catch (ex: Exception) {
            Timber.e("Unable to initialize preferences.", ex)
        }
    }

    /**
     * Creates the UI theme preference.
     */
    private fun themePreference(datastore: DataStore<Preferences>) = ListPreference(context).apply {
        key = PreferenceCompanion.THEME.name
        summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        title = "Theme"
        // TODO icon/dialog icon
        dialogTitle = "Theme"
        entries = Theme.values().map { theme -> theme.userFriendly() }.toTypedArray()
        entryValues = Theme.values().map { theme -> Json.encodeToString(theme) }.toTypedArray()
    }
}