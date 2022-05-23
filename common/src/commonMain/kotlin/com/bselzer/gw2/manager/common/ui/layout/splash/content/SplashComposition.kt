package com.bselzer.gw2.manager.common.ui.layout.splash.content

import androidx.compose.runtime.Composable
import com.bselzer.gw2.manager.common.ui.base.RouterComposition
import com.bselzer.gw2.manager.common.ui.layout.host.content.LocalSplashRouter
import com.bselzer.gw2.manager.common.ui.layout.splash.configuration.SplashConfig
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.InitializationViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.NoSplashViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.SplashViewModel

class SplashComposition : RouterComposition<SplashConfig, SplashViewModel>(
    router = { LocalSplashRouter.current }
) {
    @Composable
    override fun SplashViewModel.Content() = when (this) {
        is NoSplashViewModel -> {}
        is InitializationViewModel -> InitializationComposition(this).Content()
    }
}