package com.bselzer.gw2.manager.common.preference

import com.bselzer.ktx.settings.setting.IntSetting
import com.bselzer.ktx.settings.setting.Setting
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.SuspendSettings
import kotlin.time.DurationUnit

@OptIn(ExperimentalSettingsApi::class)
class WvwPreference(settings: SuspendSettings) {
    /**
     * The data refresh interval.
     */
    val refreshInterval: IntervalSetting = IntervalSetting(settings = settings, key = "RefreshInterval", initialAmount = 10, initialUnit = DurationUnit.MINUTES)

    /**
     * The id of the selected world.
     */
    val selectedWorld: Setting<Int> = IntSetting(settings = settings, key = "SelectedWorld")
}