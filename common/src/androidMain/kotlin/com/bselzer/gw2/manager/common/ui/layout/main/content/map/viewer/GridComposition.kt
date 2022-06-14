package com.bselzer.gw2.manager.common.ui.layout.main.content.map.viewer

import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ViewerViewModel

actual fun PlatformGridComposition(model: ViewerViewModel): GridComposition {
    return when (model.configuration.wvw.map.legacyGrid) {
        true -> BoxGridComposition(model)
        false -> MapComposeGridComposition(model)
    }
}