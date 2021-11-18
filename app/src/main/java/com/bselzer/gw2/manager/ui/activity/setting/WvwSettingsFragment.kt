package com.bselzer.gw2.manager.ui.activity.setting

import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.preference.Preference
import com.bselzer.gw2.manager.R
import com.bselzer.gw2.manager.companion.preference.WvwPreferenceCompanion
import com.bselzer.gw2.manager.ui.kodein.DIAwarePreferenceFragment
import com.bselzer.library.kotlin.extension.preference.DataStoreWrapper
import com.bselzer.library.kotlin.extension.preference.addTo
import com.bselzer.library.kotlin.extension.preference.safeLatest
import com.h6ah4i.android.preference.NumberPickerPreferenceCompat
import com.h6ah4i.android.preference.NumberPickerPreferenceDialogFragmentCompat

class WvwSettingsFragment : DIAwarePreferenceFragment() {
    private val TAG = this::class.qualifiedName!!

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = DataStoreWrapper(datastore)
        preferenceScreen = preferenceManager.createPreferenceScreen(context).apply {
            delayPreference(datastore).addTo(this)
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (parentFragmentManager.findFragmentByTag(TAG) != null) {
            // Already displaying the dialog so do nothing.
            return
        }

        // Display dialogs for custom preferences.
        if (preference is NumberPickerPreferenceCompat) {
            val fragment = NumberPickerPreferenceDialogFragmentCompat.newInstance(preference.key)
            fragment.setTargetFragment(this, 0)
            fragment.show(parentFragmentManager, TAG)
        } else {
            // Delegate non-custom preferences to the parent.
            super.onDisplayPreferenceDialog(preference)
        }
    }

    /**
     * Creates the delay preference.
     *
     * TODO be able to pick multiple DateTime components
     */
    private fun delayPreference(datastore: DataStore<Preferences>) = NumberPickerPreferenceCompat(context).apply {
        fun Int.summary(): String = "$this minutes"

        key = WvwPreferenceCompanion.REFRESH_INTERVAL.name
        title = "Refresh Interval"
        summary = datastore.safeLatest(WvwPreferenceCompanion.REFRESH_INTERVAL, 5).summary()
        setIcon(R.drawable.gw2_concentration)
        dialogTitle = "Delay in minutes"
        setDialogIcon(R.drawable.gw2_concentration)
        minValue = 1
        maxValue = 60
        onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
            if (newValue is Int) {
                summary = newValue.summary()
                true
            } else {
                false
            }
        }
    }
}