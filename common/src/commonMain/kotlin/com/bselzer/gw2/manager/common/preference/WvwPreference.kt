package com.bselzer.gw2.manager.common.preference

import com.bselzer.gw2.v2.model.world.WorldId
import com.bselzer.ktx.settings.setting.SerializableSetting
import com.bselzer.ktx.settings.setting.Setting
import com.bselzer.ktx.settings.setting.SettingWrapper
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.SuspendSettings
import kotlinx.datetime.Instant
import kotlinx.serialization.serializer
import kotlin.time.DurationUnit

@OptIn(ExperimentalSettingsApi::class)
class WvwPreference(settings: SuspendSettings) {
    /**
     * The data refresh interval.
     */
    val refreshInterval: IntervalSetting = IntervalSetting(settings = settings, key = "WvwRefreshInterval", initialAmount = 10, initialUnit = DurationUnit.MINUTES)

    /**
     * The last data refresh date/time.
     */
    val lastRefresh: SerializableSetting<Instant> = SerializableSetting(settings = settings, key = "WvwLastRefresh", defaultValue = Instant.DISTANT_PAST, serializer())

    // TODO value class settings
    /**
     * The id of the selected world.
     */
    val selectedWorld: Setting<WorldId> = object : SettingWrapper<WorldId>(settings = settings, key = "SelectedWorld", defaultValue = WorldId()) {
        override suspend fun get(): WorldId = WorldId(settings.getInt(key, defaultValue.value))
        override suspend fun getOrNull(): WorldId? = settings.getIntOrNull(key)?.let { WorldId(it) }
        override suspend fun put(value: WorldId) = settings.putInt(key, value.value)
    }
}