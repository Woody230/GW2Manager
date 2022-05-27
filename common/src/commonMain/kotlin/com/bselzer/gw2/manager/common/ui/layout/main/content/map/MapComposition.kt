package com.bselzer.gw2.manager.common.ui.layout.main.content.map

import androidx.compose.runtime.Composable
import com.bselzer.gw2.manager.common.ui.base.RouterComposition
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MapConfig
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.MapViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ObjectiveViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ViewerViewModel

class MapComposition : RouterComposition<MapConfig, MapViewModel>(
    router = { LocalMapRouter.current }
) {
    @Composable
    override fun MapViewModel.Content() = when (this) {
        is ObjectiveViewModel -> ObjectiveComposition(this).Content()
        is ViewerViewModel -> ViewerComposition(this).Content()
    }
}