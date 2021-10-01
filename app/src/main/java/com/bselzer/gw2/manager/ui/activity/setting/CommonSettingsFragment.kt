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
import com.bselzer.library.kotlin.extension.coroutine.showToast
import com.bselzer.library.kotlin.extension.preference.DataStoreWrapper
import com.bselzer.library.kotlin.extension.preference.addTo
import com.bselzer.library.kotlin.extension.preference.clear
import com.bselzer.library.kotlin.extension.preference.update
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

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
                        AppCompanion.GW2.token.information(token = token)
                    }

                    datastore.update(PreferenceCompanion.TOKEN, token, this)
                    AppCompanion.GW2 = AppCompanion.GW2.config { copy(token = token) }
                    text = token
                    Timber.d("Set client token to $token")
                } catch (ex: ClientRequestException) {
                    showToast(context, "Unable to save token: ${ex.response.readText()}", Toast.LENGTH_LONG)
                }
            }
            return@OnPreferenceChangeListener false
        }
    }
}