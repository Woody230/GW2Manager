package com.bselzer.gw2.manager.common.ui.layout.host.viewmodel

import com.arkivanov.decompose.router.Router
import com.arkivanov.decompose.router.activeChild
import com.arkivanov.decompose.router.bringToFront
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.DialogViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.NoDialogViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.WorldSelectionViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MainConfig
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.*
import com.bselzer.gw2.manager.common.ui.layout.splash.configuration.SplashConfig
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.InitializationViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.NoSplashViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.SplashViewModel
import com.bselzer.ktx.logging.Logger
import kotlinx.coroutines.CoroutineScope

class HostViewModel(context: AppComponentContext) : ViewModel(context) {
    val dialogRouter: Router<DialogConfig, DialogViewModel> = context.createRouter(
        initialStack = { listOf(DialogConfig.NoDialogConfig) },
        configurationClass = DialogConfig::class,
        key = "Dialog",
        childFactory = { state, context ->
            Logger.d { "Dialog router: ${state::class.simpleName}" }
            when (state) {
                DialogConfig.NoDialogConfig -> NoDialogViewModel(context)
                DialogConfig.WorldSelectionConfig -> WorldSelectionViewModel(context)
            }
        }
    )

    val mainRouter: Router<MainConfig, MainViewModel> = context.createRouter(
        initialStack = { listOf(MainConfig.ModuleConfig) },
        configurationClass = MainConfig::class,
        key = "Main",
        childFactory = { state, context ->
            Logger.d { "Main router: ${state::class.simpleName}" }
            when (state) {
                MainConfig.AboutConfig -> AboutViewModel(context)
                MainConfig.CacheConfig -> CacheViewModel(context)
                MainConfig.LicenseConfig -> LicenseViewModel(context)
                MainConfig.ModuleConfig -> ModuleViewModel(context)
                MainConfig.SettingsConfig -> SettingsViewModel(context)
                MainConfig.WvwMapConfig -> WvwMapViewModel(context)
                MainConfig.WvwMatchConfig -> WvwMatchViewModel(context)
            }
        }
    )

    val splashRouter: Router<SplashConfig, SplashViewModel> = context.createRouter(
        initialStack = { listOf(SplashConfig.NoSplashConfig, SplashConfig.InitializationConfig) },
        configurationClass = SplashConfig::class,
        key = "Splash",
        childFactory = { state, context ->
            Logger.d { "Splash router: ${state::class.simpleName}" }
            when (state) {
                SplashConfig.NoSplashConfig -> NoSplashViewModel(context)
                SplashConfig.InitializationConfig -> InitializationViewModel(context)
            }
        }
    )

    val scaffold: ScaffoldViewModel = ScaffoldViewModel(context)

    /**
     * Handles back navigation between the routers.
     *
     * @return whether the back press is handled
     */
    fun onBackPressed(scope: CoroutineScope): Boolean = when {
        // If we are showing a dialog, then close it.
        dialogRouter.activeChild.instance !is NoDialogViewModel -> {
            dialogRouter.bringToFront(DialogConfig.NoDialogConfig)
            true
        }

        // Otherwise if the drawer is open, then close it.
        scaffold.drawer.state.isOpen -> {
            with(scaffold.drawer) {
                scope.close()
                true
            }
        }

        // Otherwise if we aren't on the module page then go back to it.
        mainRouter.activeChild.instance !is ModuleViewModel -> {
            mainRouter.bringToFront(MainConfig.ModuleConfig)
            true
        }

        // Otherwise let the system propagate backing out.
        else -> false
    }
}