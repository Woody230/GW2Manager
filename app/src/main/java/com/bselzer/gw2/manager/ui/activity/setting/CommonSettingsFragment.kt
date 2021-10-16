package com.bselzer.gw2.manager.ui.activity.setting

import android.os.Bundle
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.companion.AppCompanion
import com.bselzer.gw2.manager.companion.preference.PreferenceCompanion
import com.bselzer.gw2.manager.companion.preference.WvwPreferenceCompanion.SELECTED_WORLD
import com.bselzer.library.gw2.v2.model.account.token.TokenInfo
import com.bselzer.library.gw2.v2.model.enumeration.extension.account.permissions
import com.bselzer.library.gw2.v2.scope.core.Permission
import com.bselzer.library.kotlin.extension.coroutine.showToast
import com.bselzer.library.kotlin.extension.preference.*
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.time.ExperimentalTime

class CommonSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val datastore = AppCompanion.DATASTORE
        preferenceManager.preferenceDataStore = DataStoreWrapper(datastore)
        preferenceScreen = preferenceManager.createPreferenceScreen(context).apply {
            tokenPreference(datastore).addTo(this)
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
                        val tokenInfo = AppCompanion.GW2.token.information(token = token)
                        initializePreferences(tokenInfo)
                        datastore.update(PreferenceCompanion.TOKEN, token)
                        AppCompanion.GW2 = AppCompanion.GW2.config { copy(token = token) }
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
            val client = AppCompanion.GW2
            val datastore = AppCompanion.DATASTORE

            // TODO scope processor to automatically verify permissions
            if (permissions.contains(Permission.ACCOUNT)) {
                val account = client.account.account(token)
                datastore.initialize(SELECTED_WORLD, account.world)
            }
        } catch (ex: Exception) {
            Timber.e("Unable to initialize preferences.", ex)
        }
    }
}