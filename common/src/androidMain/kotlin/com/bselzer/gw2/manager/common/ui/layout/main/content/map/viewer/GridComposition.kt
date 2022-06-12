package com.bselzer.gw2.manager.common.ui.layout.main.content.map.viewer

import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ViewerViewModel

actual fun PlatformGridComposition(model: ViewerViewModel): GridComposition = MapComposeGridComposition(model)