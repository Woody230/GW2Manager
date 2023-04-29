package com.bselzer.gw2.manager.common.ui.layout.main.content.map

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.bselzer.gw2.manager.common.ui.base.Router
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MapConfig
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.MapViewModel

val LocalMapRouter: ProvidableCompositionLocal<Router<MapConfig, MapViewModel>> = compositionLocalOf {
    throw NotImplementedError("Map router is not initialized")
}