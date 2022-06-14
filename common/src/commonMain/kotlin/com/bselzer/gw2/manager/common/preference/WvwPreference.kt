package com.bselzer.gw2.manager.common.preference

import com.bselzer.gw2.v2.model.world.WorldId
import com.bselzer.ktx.settings.setting.InitialDurationSetting
import com.bselzer.ktx.settings.setting.IntIdentifierSetting
import com.bselzer.ktx.settings.setting.SerializableSetting
import com.bselzer.ktx.settings.setting.Setting
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
    val refreshInterval: InitialDurationSetting = InitialDurationSetting(
        settings = settings,
        key = "WvwRefreshInterval",
        initialAmount = 10,
        initialUnit = DurationUnit.MINUTES
    )

    /**
     * The last data refresh date/time.
     */
    val lastRefresh: SerializableSetting<Instant> = SerializableSetting(
        settings = settings,
        key = "WvwLastRefresh",
        defaultValue = Instant.DISTANT_FUTURE, serializer()
    )

    /**
     * The id of the selected world.
     */
    val selectedWorld: Setting<WorldId> = IntIdentifierSetting(
        settings = settings,
        key = "SelectedWorld",
        create = { WorldId(it) }
    )
}