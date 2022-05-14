package com.bselzer.gw2.manager.common.ui.layout.splash.content

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.router.RouterState
import com.arkivanov.decompose.value.Value
import com.bselzer.gw2.manager.common.ui.base.ChildComposition
import com.bselzer.gw2.manager.common.ui.layout.splash.configuration.SplashConfig
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.DefaultViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.InitializationViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.SplashViewModel
import com.bselzer.ktx.logging.Logger

class SplashComposition(
    private val onFinish: () -> Unit
) : ChildComposition<SplashConfig, SplashViewModel> {
    @Composable
    override fun Content(state: Value<RouterState<SplashConfig, SplashViewModel>>) = Children(
        routerState = state,
    ) { child ->
        when (val model = child.instance) {
            is DefaultViewModel -> {
                Logger.d { "Splash screen is removed." }
            }
            is InitializationViewModel -> InitializationComposition(onFinish).Content(model)
        }
    }
}