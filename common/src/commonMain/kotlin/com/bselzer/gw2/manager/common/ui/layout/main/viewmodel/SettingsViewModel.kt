package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.WvwResources
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.interval.WvwIntervalLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.interval.WvwIntervalResources
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.map.MapLabelLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.map.MapLabelResources
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.map.ZoomLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.map.ZoomResources
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.ColorViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.LanguageViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.ThemeViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.TokenViewModel
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.ktx.datetime.format.DurationBound
import com.bselzer.ktx.resource.KtxResources
import com.bselzer.ktx.resource.strings.stringResource
import com.bselzer.ktx.settings.compose.defaultState
import com.bselzer.ktx.settings.compose.safeState
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch
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

    private val zoom: MutableState<Int?> = mutableStateOf(null)
    val zoomResources
        @Composable
        get() = ZoomResources(
            image = Gw2Resources.images.gift_of_exploration,
            title = AppResources.strings.default_zoom_level.desc(),
            subtitle = preferences.wvw.zoom.safeState().value.toString().desc(),
        )

    val zoomLogic: ZoomLogic
        get() {
            val range = repositories.selectedWorld.zoomRange
            return ZoomLogic(
                // TODO default to the actual preference instead of initial?
                amount = zoom.value ?: preferences.wvw.zoom.defaultValue,
                amountRange = range,
                onValueChange = { zoom.value = it.coerceIn(range) },
                onSave = {
                    zoom.value?.let { updateZoom(it) }
                },
                onReset = { updateZoom(preferences.wvw.zoom.defaultValue) },
                clearInput = { zoom.value = null }
            )
        }

    private suspend fun updateZoom(zoom: Int) {
        val bounded = zoom.coerceIn(repositories.selectedWorld.zoomRange)
        preferences.wvw.zoom.set(bounded)
        repositories.selectedWorld.updateZoom(bounded)
    }

    val mapLabelResources
        @Composable
        get() = MapLabelResources(
            image = Gw2Resources.images.gift_of_exploration,
            title = AppResources.strings.team_label.desc(),
            subtitle = preferences.wvw.showMapLabel.safeState().value.stringResource().desc()
        )

    val mapLabelLogic
        @Composable
        get() = run {
            val scope = rememberCoroutineScope()
            MapLabelLogic(
                checked = preferences.wvw.showMapLabel.safeState().value,
                onCheckedChange = { checked ->
                    scope.launch { preferences.wvw.showMapLabel.set(checked) }
                }
            )
        }

    val colors: List<ColorViewModel>
        get() = mapTypes.map { mapType ->
            ColorViewModel(
                context = this,
                mapType = mapType
            )
        }
}