package com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.status.StatusLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.status.StatusResources
import com.bselzer.ktx.resource.KtxResources
import com.bselzer.ktx.resource.strings.stringResource
import com.bselzer.ktx.settings.safeState
import com.bselzer.ktx.settings.setting.Setting
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch

class StatusViewModel(
    context: AppComponentContext
): ViewModel(context) {
    private val setting: Setting<Boolean> = preferences.common.showApiStatus

    val resources: StatusResources
        @Composable
        get() = StatusResources(
            image = KtxResources.images.ic_settings,
            title = KtxResources.strings.api_status.desc(),
            subtitle = setting.safeState().value.stringResource().desc()
        )

    val logic: StatusLogic
        @Composable
        get() {
            val scope = rememberCoroutineScope()
            return StatusLogic(
                checked = setting.safeState().value,
                onCheckedChange = { checked ->
                    scope.launch { setting.set(checked) }
                }
            )
        }
}