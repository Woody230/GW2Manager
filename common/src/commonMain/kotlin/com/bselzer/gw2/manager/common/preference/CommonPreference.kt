package com.bselzer.gw2.manager.common.preference

import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.gw2.v2.client.model.Token
import com.bselzer.ktx.intl.Locale
import com.bselzer.ktx.intl.Localizer
import com.bselzer.ktx.serialization.serializer.LocaleSerializer
import com.bselzer.ktx.settings.setting.BooleanSetting
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
    val appVersion: Setting<Int> = IntSetting(
        settings = settings,
        key = "ApplicationVersion"
    )

    /**
     * The GW2 build version.
     */
    val buildNumber: Setting<Int> = IntSetting(
        settings = settings,
        key = "BuildNumber"
    )

    /**
     * The GW2 API token or api key.
     */
    val token: Setting<Token> = SerializableSetting(
        settings = settings,
        key = "Token",
        defaultValue = Token(""),
        serializer = serializer()
    )

    /**
     * The UI theme.
     */
    val theme: Setting<Theme> = SerializableSetting(
        settings = settings,
        key = "Theme",
        defaultValue = Theme.LIGHT,
        serializer = serializer()
    )

    /**
     * The locale indicating the language to display information in.
     */
    val locale: Setting<Locale> = SerializableSetting(
        settings = settings,
        key = "Locale",
        defaultValue = Localizer.ENGLISH,
        LocaleSerializer()
    )

    /**
     * Whether the API status should be displayed.
     */
    val showApiStatus: Setting<Boolean> = BooleanSetting(
        settings = settings,
        key = "ShowApiStatus",
        defaultValue = true
    )
}