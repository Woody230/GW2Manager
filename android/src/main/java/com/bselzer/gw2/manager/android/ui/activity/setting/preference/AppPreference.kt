package com.bselzer.gw2.manager.android.ui.activity.setting.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bselzer.library.kotlin.extension.function.objects.enumValueOrNull
import com.bselzer.library.kotlin.extension.preference.DataStoreWrapper
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
abstract class AppPreference(protected val datastore: DataStore<Preferences>) {
    /**
     * The datastore preference wrapper.
     */
    protected val wrapper = DataStoreWrapper(datastore)

    /**
     * @return the string preference associated with the [key], or the [defaultValue] if there is no value
     */
    protected fun getNullString(key: Preferences.Key<String>, defaultValue: String? = null): String? = wrapper.getString(key.name, defaultValue)

    /**
     * @return the string preference associated with the [key], or the [defaultValue] if there is no value
     */
    protected fun getSafeString(key: Preferences.Key<String>, defaultValue: String? = null): String? = getNullString(key, defaultValue) ?: defaultValue

    /**
     * Sets the string value to the preference associated with the [key]
     */
    protected fun putString(key: Preferences.Key<String>, value: String?): Unit = wrapper.putString(key.name, value)

    /**
     * @return the integer preference associated with the [key], or the [defaultValue] if there is no value
     */
    protected fun getInt(key: Preferences.Key<Int>, defaultValue: Int = 0): Int = wrapper.getInt(key.name, defaultValue)

    /**
     * Sets the integer value to the preference associated with the [key]
     */
    protected fun putInt(key: Preferences.Key<Int>, value: Int): Unit = wrapper.putInt(key.name, value)

    /**
     * @return the enum preference associated with the string [key], or the [defaultValue] if there is no value
     */
    protected inline fun <reified T : Enum<T>> getEnum(key: Preferences.Key<String>, defaultValue: T): T =
        wrapper.getString(key.name, null)?.enumValueOrNull<T>() ?: defaultValue

    /**
     * Sets the enum value to a string preference associated with the [key]
     */
    protected inline fun <reified T : Enum<T>> putEnum(key: Preferences.Key<String>, value: T): Unit = wrapper.putString(key.name, Json.encodeToString(value))

    /**
     * @return the duration preference associated with the string [key], or the [defaultValue] if there is no value
     */
    protected fun getDuration(key: Preferences.Key<String>, defaultValue: Duration): Duration {
        val stored = wrapper.getString(key.name, null) ?: return defaultValue
        return Duration.parseOrNull(stored) ?: defaultValue
    }

    /**
     * Sets the enum value to a string preference associated with the [key]
     */
    protected fun putDuration(key: Preferences.Key<String>, value: Duration): Unit = wrapper.putString(key.name, value.toIsoString())
}