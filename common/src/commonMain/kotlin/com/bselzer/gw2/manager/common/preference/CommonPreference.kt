package com.bselzer.gw2.manager.common.preference

import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.gw2.v2.model.enumeration.Language
import com.bselzer.ktx.settings.setting.IntSetting
import com.bselzer.ktx.settings.setting.SerializableSetting
import com.bselzer.ktx.settings.setting.Setting
import com.bselzer.ktx.settings.setting.StringSetting
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.SuspendSettings
import kotlinx.serialization.serializer

@OptIn(ExperimentalSettingsApi::class)
class CommonPreference(settings: SuspendSettings) {
    /**
     * The version of this application.
     */
    val appVersion: Setting<Int> = IntSetting(settings = settings, key = "ApplicationVersion")

    /**
     * The GW2 build version.
     */
    val buildNumber: Setting<Int> = IntSetting(settings = settings, key = "BuildNumber")

    /**
     * The GW2 API token or api key.
     */
    val token: Setting<String> = StringSetting(settings = settings, key = "Token")

    /**
     * The UI theme.
     */
    val theme: Setting<Theme> = SerializableSetting(settings = settings, key = "Theme", defaultValue = Theme.LIGHT, serializer = serializer())

    /**
     * The language to display information in.
     */
    val language: Setting<Language> = SerializableSetting(settings = settings, key = "Language", defaultValue = Language.ENGLISH, serializer())
}