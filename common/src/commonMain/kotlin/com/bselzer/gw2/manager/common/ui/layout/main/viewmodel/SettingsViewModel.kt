package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.WvwResources
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.interval.WvwIntervalLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.interval.WvwIntervalResources
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.*
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.ktx.datetime.format.DurationBound
import com.bselzer.ktx.resource.KtxResources
import com.bselzer.ktx.resource.strings.stringResource
import com.bselzer.ktx.settings.compose.defaultState
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class SettingsViewModel(context: AppComponentContext) : MainViewModel(context) {
    override val title: StringDesc = KtxResources.strings.settings.desc()

    val theme: ThemeViewModel
        get() = ThemeViewModel(context = this)

    val token: TokenViewModel
        get() = TokenViewModel(context = this)

    val wvwResources
        @Composable
        get() = run {
            val interval = preferences.wvw.refreshInterval.defaultState().value
            WvwResources(
                image = Gw2Resources.images.rank_dolyak,
                title = Gw2Resources.strings.wvw.desc(),
                interval = WvwIntervalResources(
                    image = Gw2Resources.images.concentration,
                    title = KtxResources.strings.refresh_interval.desc(),

                    // The notation given should be acceptable for all of the supported localizations.
                    subtitle = interval.toString().desc(),
                    label = { unit -> unit.stringResource().desc() }
                )
            )
        }

    // TODO default to the actual preference instead of initial?
    private val intervalAmount: MutableState<Int> = mutableStateOf(preferences.wvw.refreshInterval.initialAmount)
    private val intervalUnit: MutableState<DurationUnit> = mutableStateOf(preferences.wvw.refreshInterval.initialUnit)
    private val intervalBound = DurationBound(min = 30.seconds)
    val intervalLogic
        get() = WvwIntervalLogic(
            amount = intervalBound.minBind(intervalAmount.value, intervalUnit.value),
            unit = intervalUnit.value,
            amountRange = intervalBound.minRange(intervalUnit.value),
            units = listOf(DurationUnit.SECONDS, DurationUnit.MINUTES, DurationUnit.HOURS, DurationUnit.DAYS),
            onValueChange = { amount, unit ->
                intervalAmount.value = intervalBound.minBind(amount, unit)
                intervalUnit.value = unit
            },
            onSave = {
                val unit = intervalUnit.value
                val amount = intervalBound.minBind(intervalAmount.value, unit)
                preferences.wvw.refreshInterval.set(amount.toDuration(unit))
            },
            onReset = { preferences.wvw.refreshInterval.remove() },
            clearInput = {
                intervalAmount.value = preferences.wvw.refreshInterval.initialAmount
                intervalUnit.value = preferences.wvw.refreshInterval.initialUnit
            }
        )

    val language: LanguageViewModel
        get() = LanguageViewModel(context = this)

    val zoom: ZoomViewModel
        get() = ZoomViewModel(context = this)

    val mapLabel: MapLabelViewModel
        get() = MapLabelViewModel(context = this)

    val colors: List<ColorViewModel>
        get() = mapTypes.map { mapType ->
            ColorViewModel(
                context = this,
                mapType = mapType
            )
        }
}