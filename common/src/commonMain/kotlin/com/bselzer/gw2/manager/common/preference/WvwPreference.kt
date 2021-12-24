package com.bselzer.gw2.manager.common.preference

import com.bselzer.ktx.settings.setting.DurationSetting
import com.bselzer.ktx.settings.setting.IntSetting
import com.bselzer.ktx.settings.setting.delegate.SafeSetting
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.SuspendSettings
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalSettingsApi::class)
class WvwPreference(settings: SuspendSettings) {
    /**
     * The data refresh interval.
     */
    @OptIn(ExperimentalTime::class)
    val refreshInterval: SafeSetting<Duration> = DurationSetting(settings = settings, key = "RefreshInterval", defaultValue = 10.minutes).safe()

    /**
     * The default data refresh interval date/time component.
     */
    @OptIn(ExperimentalTime::class)
    val refreshIntervalDefaultUnit: DurationUnit = DurationUnit.MINUTES

    /**
     * The id of the selected world.
     */
    val selectedWorld: SafeSetting<Int> = IntSetting(settings = settings, key = "SelectedWorld").safe()
}