package com.bselzer.library.kotlin.extension.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Updates the preference value for [key] to [value] if it is not null. Otherwise, the key is removed.
 * @return the coroutine job
 */
inline fun <reified TNullable, reified TNullSafe : TNullable> DataStore<Preferences>.update(key: Preferences.Key<TNullSafe>, value: TNullable, scope: CoroutineScope) =
    scope.launch {
        edit { pref -> value?.let { pref[key] = it as TNullSafe } ?: pref.remove(key) }
    }

/**
 * Gets the preference value for [key] and collects it as a state, starting with the given [initialValue] and defaulting null values to the [defaultValue].
 * @return the preference flow as a state
 */
@Composable
inline fun <reified TNullSafe> DataStore<Preferences>.collectAsSafeState(key: Preferences.Key<TNullSafe>, initialValue: TNullSafe, defaultValue: TNullSafe): State<TNullSafe> {
    return data.map { pref -> pref[key] ?: defaultValue }.collectAsState(initial = initialValue)
}

/**
 * Gets the preference value for [key] and collects it as a state, starting with the given [initialValue] and defaulting null values to the [defaultValue].
 * @return the preference flow as a state
 */
@Composable
inline fun <reified TNullable, reified TNullSafe : TNullable> DataStore<Preferences>.collectAsNullState(
    key: Preferences.Key<TNullSafe>,
    initialValue: TNullable,
    defaultValue: TNullable
): State<TNullable> {
    return data.map { pref -> pref[key] ?: defaultValue }.collectAsState(initial = initialValue)
}