package com.bselzer.gw2.manager.common.preference

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.configuration.Configuration
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.world.WorldId
import com.bselzer.ktx.compose.ui.graphics.color.Hex
import com.bselzer.ktx.compose.ui.graphics.color.color
import com.bselzer.ktx.serialization.compose.serializer.ColorSerializer
import com.bselzer.ktx.settings.setting.*
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.SuspendSettings
import kotlinx.datetime.Instant
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.time.DurationUnit

@OptIn(ExperimentalSettingsApi::class)
class WvwPreference(settings: SuspendSettings, configuration: Configuration) {
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
    val lastRefresh: Setting<Instant> = SerializableSetting(
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

    /**
     * The objective owners mapped to the color to display images and text in.
     */
    val colors: Setting<Map<WvwObjectiveOwner, Color>> = SerializableSetting(
        settings = settings,
        key = "BorderlandColors",
        defaultValue = configuration.wvw.objectives.colors.associate { color -> color.owner to Hex(color.type).color() },
        serializer = SerializersModule {
            contextual(Color::class, ColorSerializer())
        }.serializer()
    )

    /**
     * The default zoom level to use when initially loading the grid.
     */
    val zoom: Setting<Int> = IntSetting(
        settings = settings,
        key = "GridZoom",
        defaultValue = configuration.wvw.map.zoom.default,
    )

    /**
     * Whether the map label over the borderlands should be displayed.
     */
    val showMapLabel: Setting<Boolean> = BooleanSetting(
        settings = settings,
        key = "ShowMapLabel",
        defaultValue = true,
    )
}