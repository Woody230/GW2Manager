package com.bselzer.gw2.manager.common.ui.layout.main.content.map.viewer

import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ViewerViewModel

// TODO Desktop: waiting for jetbrains compose support of MapCompose https://github.com/p-lr/MapCompose/issues/1
actual fun PlatformGridComposition(model: ViewerViewModel): GridComposition = BoxGridComposition(model)