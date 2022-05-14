package com.bselzer.gw2.manager.common.ui.layout.splash.content

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.router.Router
import com.arkivanov.decompose.router.bringToFront
import com.bselzer.gw2.manager.common.ui.base.RouterComposition
import com.bselzer.gw2.manager.common.ui.layout.splash.configuration.SplashConfig
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.DefaultViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.InitializationViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.SplashViewModel

class SplashComposition(
    router: Router<SplashConfig, SplashViewModel>
) : RouterComposition<SplashConfig, SplashViewModel>(router) {
    @Composable
    override fun SplashViewModel.Content() = when (this) {
        is DefaultViewModel -> {}
        is InitializationViewModel -> InitializationComposition {
            // Don't show the splash screen once initialization is finished.
            router.bringToFront(SplashConfig.DefaultConfig)
        }.Content(this)
    }
}