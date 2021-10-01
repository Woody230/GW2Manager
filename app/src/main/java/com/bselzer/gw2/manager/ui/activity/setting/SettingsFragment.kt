package com.bselzer.gw2.manager.ui.activity.setting

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.companion.AppCompanion
import com.bselzer.gw2.manager.ui.activity.wvw.WvwActivity
import com.bselzer.library.kotlin.extension.preference.DataStoreWrapper
import com.bselzer.library.kotlin.extension.preference.addTo

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val datastore = AppCompanion.DATASTORE
        preferenceManager.preferenceDataStore = DataStoreWrapper(datastore)
        preferenceScreen = preferenceManager.createPreferenceScreen(context).apply {
            commonScreen().addTo(this)
            wvwScreen().addTo(this)
        }
    }

    /**
     * Creates the common preference screen for information used by all aspects of this app.
     */
    private fun commonScreen() = preferenceManager.createPreferenceScreen(context).apply {
        fragment = CommonSettingsFragment::class.qualifiedName
        title = "Common"
        summary = "Token"

        // TODO better icon
        setIcon(R.drawable.gw2_black_lion_key)
    }

    /**
     * Creates the WvW preference screen for information used by the [WvwActivity].
     */
    private fun wvwScreen() = preferenceManager.createPreferenceScreen(context).apply {
        fragment = WvwSettingsFragment::class.qualifiedName
        title = "World vs. World"
        setIcon(R.drawable.gw2_rank_dolyak)
    }
}