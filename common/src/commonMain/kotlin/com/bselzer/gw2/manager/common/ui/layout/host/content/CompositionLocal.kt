package com.bselzer.gw2.manager.common.ui.layout.host.content

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.bselzer.gw2.manager.common.ui.base.Router
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.DialogViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MainConfig
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.MainViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.configuration.SplashConfig
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.SplashViewModel

val LocalDialogRouter: ProvidableCompositionLocal<Router<DialogConfig, DialogViewModel>> = compositionLocalOf {
    throw NotImplementedError("Dialog router is not initialized")
}

val LocalMainRouter: ProvidableCompositionLocal<Router<MainConfig, MainViewModel>> = compositionLocalOf {
    throw NotImplementedError("Main router is not initialized.")
}

val LocalSplashRouter: ProvidableCompositionLocal<Router<SplashConfig, SplashViewModel>> = compositionLocalOf {
    throw NotImplementedError("Splash router is not initialized.")
}