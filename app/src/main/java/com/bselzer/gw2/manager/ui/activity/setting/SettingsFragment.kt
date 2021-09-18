package com.bselzer.gw2.manager.ui.activity.setting

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.companion.AppCompanion
import com.bselzer.library.kotlin.extension.preference.DataStoreWrapper

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        preferenceManager.preferenceDataStore = DataStoreWrapper(AppCompanion.DATASTORE)

        val screen = preferenceManager.createPreferenceScreen(context)

        // TODO store token instead of api key
        EditTextPreference(context).apply {
            key = "ApiKey"
            summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            title = "API Key"
            setIcon(R.drawable.gw2_black_lion_key)
            dialogTitle = "API Key"
            setDialogIcon(R.drawable.gw2_black_lion_key)
        }.addTo(screen)

        preferenceScreen = screen
    }

    /**
     * Adds [this] to the [screen]
     */
    private fun Preference.addTo(screen: PreferenceScreen) = screen.addPreference(this)
}