package com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.interval.RefreshIntervalLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.interval.RefreshIntervalResources
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.ktx.datetime.format.DurationBound
import com.bselzer.ktx.resource.KtxResources
import com.bselzer.ktx.resource.strings.localized
import com.bselzer.ktx.resource.strings.stringResource
import com.bselzer.ktx.settings.safeState
import com.bselzer.ktx.settings.setting.InitialDurationSetting
import dev.icerock.moko.resources.desc.desc
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class RefreshIntervalViewModel(
    context: AppComponentContext,
    private val setting: InitialDurationSetting,
    private val intervalBound: DurationBound,
    private val units: List<DurationUnit>,
) : ViewModel(context) {
    // TODO default to the actual preference instead of initial?
    private val intervalAmount: MutableState<Int> = mutableStateOf(setting.initialAmount)
    private val intervalUnit: MutableState<DurationUnit> = mutableStateOf(setting.initialUnit)

    // TODO better way to manage the mixed usages of these labels in composable and non-composable contexts?
    val labels: Map<DurationUnit, String>
        @Composable
        get() = logic.units.associateWith { unit -> resources.label(unit).localized() }

    val logic: RefreshIntervalLogic
        get() = RefreshIntervalLogic(
            amount = intervalBound.minBind(intervalAmount.value, intervalUnit.value),
            unit = intervalUnit.value,
            amountRange = intervalBound.minRange(intervalUnit.value),
            units = units,
            onValueChange = { amount, unit ->
                intervalAmount.value = intervalBound.minBind(amount, unit)
                intervalUnit.value = unit
            },
            onSave = {
                val unit = intervalUnit.value
                val amount = intervalBound.minBind(intervalAmount.value, unit)
                setting.set(amount.toDuration(unit))
            },
            onReset = { setting.remove() },
            clearInput = {
                intervalAmount.value = setting.initialAmount
                intervalUnit.value = setting.initialUnit
            }
        )

    val resources: RefreshIntervalResources
        @Composable
        get() = RefreshIntervalResources(
            image = Gw2Resources.images.concentration,
            title = KtxResources.strings.refresh_interval.desc(),

            // The notation given should be acceptable for all of the supported localizations.
            subtitle = setting.safeState().value.toString().desc(),
            label = { unit -> unit.stringResource().desc() }
        )
}