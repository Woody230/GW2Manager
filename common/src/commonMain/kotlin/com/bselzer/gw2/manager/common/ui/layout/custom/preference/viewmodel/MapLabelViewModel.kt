package com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.map.MapLabelLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.map.MapLabelResources
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.ktx.resource.strings.stringResource
import com.bselzer.ktx.settings.compose.safeState
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch

class MapLabelViewModel(
    context: AppComponentContext
) : ViewModel(context) {
    val resources: MapLabelResources
        @Composable
        get() = MapLabelResources(
            image = Gw2Resources.images.gift_of_exploration,
            title = AppResources.strings.team_label.desc(),
            subtitle = preferences.wvw.showMapLabel.safeState().value.stringResource().desc()
        )

    val logic: MapLabelLogic
        @Composable
        get() {
            val scope = rememberCoroutineScope()
            return MapLabelLogic(
                checked = preferences.wvw.showMapLabel.safeState().value,
                onCheckedChange = { checked ->
                    scope.launch { preferences.wvw.showMapLabel.set(checked) }
                }
            )
        }
}