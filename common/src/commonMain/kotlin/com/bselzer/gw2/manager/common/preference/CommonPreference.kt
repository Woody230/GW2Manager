package com.bselzer.gw2.manager.common.preference

import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.settings.setting.IntSetting
import com.bselzer.ktx.settings.setting.SerializableSetting
import com.bselzer.ktx.settings.setting.StringSetting
import com.bselzer.ktx.settings.setting.delegate.NullSetting
import com.bselzer.ktx.settings.setting.delegate.SafeSetting
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.SuspendSettings
import kotlinx.serialization.serializer

@OptIn(ExperimentalSettingsApi::class)
class CommonPreference(settings: SuspendSettings) {
    /**
     * The GW2 build version.
     */
    val buildNumber: SafeSetting<Int> = IntSetting(settings = settings, key = "BuildNumber").safe()

    /**
     * The GW2 API token or api key.
     */
    val token: NullSetting<String> = StringSetting(settings = settings, key = "Token").nullable()

    /**
     * The UI theme.
     */
    val theme: SafeSetting<Theme> = SerializableSetting(settings = settings, key = "Theme", defaultValue = Theme.LIGHT, serializer = serializer()).safe()
}