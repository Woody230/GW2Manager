package com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.map.ZoomLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.map.ZoomResources
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.ktx.settings.compose.safeState
import com.bselzer.ktx.settings.setting.Setting
import dev.icerock.moko.resources.desc.desc

class ZoomViewModel(
    context: AppComponentContext
) : ViewModel(context) {
    private val setting: Setting<Int> = preferences.wvw.zoom
    private val intermediary: MutableState<Int?> = mutableStateOf(null)

    val resources: ZoomResources
        @Composable
        get() = ZoomResources(
            image = Gw2Resources.images.gift_of_exploration,
            title = AppResources.strings.default_zoom_level.desc(),
            subtitle = setting.safeState().value.toString().desc(),
        )

    val logic: ZoomLogic
        get() {
            val range = repositories.selectedWorld.zoomRange
            return ZoomLogic(
                // TODO default to the actual preference instead of initial?
                amount = intermediary.value ?: setting.defaultValue,
                amountRange = range,
                onValueChange = { intermediary.value = it.coerceIn(range) },
                onSave = {
                    intermediary.value?.let { updateZoom(it) }
                },
                onReset = { updateZoom(setting.defaultValue) },
                clearInput = { intermediary.value = null }
            )
        }

    private suspend fun updateZoom(zoom: Int) {
        val bounded = zoom.coerceIn(repositories.selectedWorld.zoomRange)
        setting.set(bounded)
        repositories.selectedWorld.updateZoom(bounded)
    }
}