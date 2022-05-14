package com.bselzer.gw2.manager.common.ui.layout.host.viewmodel

import com.arkivanov.decompose.router.Router
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.context.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MainConfig
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.MainViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.ModuleViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.SettingsViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.configuration.SplashConfig
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.DefaultViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.InitializationViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.SplashViewModel

class HostViewModel(context: AppComponentContext) : ViewModel(context) {
    val mainRouter: Router<MainConfig, MainViewModel> = context.createRouter(
        initialStack = { listOf(MainConfig.ModuleConfig) },
        configurationClass = MainConfig::class,
        key = "Main",
        childFactory = { state, context ->
            when (state) {
                MainConfig.ModuleConfig -> ModuleViewModel(context)
                MainConfig.SettingsConfig -> SettingsViewModel(context)
            }
        }
    )

    val splashRouter: Router<SplashConfig, SplashViewModel> = context.createRouter(
        initialStack = { listOf(SplashConfig.DefaultConfig, SplashConfig.InitializationConfig) },
        configurationClass = SplashConfig::class,
        key = "Splash",
        childFactory = { state, context ->
            when (state) {
                SplashConfig.DefaultConfig -> DefaultViewModel(context)
                SplashConfig.InitializationConfig -> InitializationViewModel(context)
            }
        }
    )
}