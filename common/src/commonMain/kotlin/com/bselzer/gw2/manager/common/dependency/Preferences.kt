package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.manager.common.preference.CommonPreference
import com.bselzer.gw2.manager.common.preference.WvwPreference
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.SuspendSettings

@OptIn(ExperimentalSettingsApi::class)
data class Preferences(
    val settings: SuspendSettings,
    val common: CommonPreference,
    val wvw: WvwPreference
)