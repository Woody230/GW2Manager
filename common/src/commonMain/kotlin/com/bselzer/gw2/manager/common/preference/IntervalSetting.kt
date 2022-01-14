package com.bselzer.gw2.manager.common.preference

import com.bselzer.ktx.settings.setting.DurationSetting
import com.bselzer.ktx.settings.setting.Setting
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.SuspendSettings
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(ExperimentalSettingsApi::class)
class IntervalSetting(settings: SuspendSettings, override val key: String, val initialAmount: Int, val initialUnit: DurationUnit) :
    Setting<Duration> by DurationSetting(settings = settings, key = key, defaultValue = initialAmount.toDuration(initialUnit))
