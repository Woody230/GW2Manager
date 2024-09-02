package com.bselzer.gw2.manager.common.ui.layout.host.viewmodel

import com.arkivanov.decompose.router.stack.bringToFront
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.Router
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.DialogViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.NoDialogViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.WorldSelectionViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MainConfig
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.AboutViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.CacheViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.LicenseViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.MainViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.SettingsViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMapViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMatchContestedAreasViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMatchOverviewViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMatchStatisticsViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.configuration.SplashConfig
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.InitializationViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.NoSplashViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.SplashViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.serializer

class HostViewModel(context: AppComponentContext) : ViewModel(context) {
    val dialogRouter: Router<DialogConfig, DialogViewModel> = context.createRouter(
        initialStack = { listOf(DialogConfig.NoDialogConfig) },
        serializer = serializer(),
        key = "Dialog",
        childFactory = { state, context ->
            when (state) {
                DialogConfig.NoDialogConfig -> NoDialogViewModel(context)
                DialogConfig.WorldSelectionConfig -> WorldSelectionViewModel(context)
            }
        }
    )

    // TODO better way to handle this?
    private val showDialog: (DialogConfig) -> Unit = { config ->
        dialogRouter.bringToFront(config)
    }

    val mainRouter: Router<MainConfig, MainViewModel> = context.createRouter(
        initialStack = { listOf(MainConfig.WvwMatchOverviewConfig) },
        serializer = serializer(),
        key = "Main",
        childFactory = { state, context ->
            when (state) {
                MainConfig.AboutConfig -> AboutViewModel(context)
                MainConfig.CacheConfig -> CacheViewModel(context)
                MainConfig.LicenseConfig -> LicenseViewModel(context)
                MainConfig.WvwMatchOverviewConfig -> WvwMatchOverviewViewModel(context)
                MainConfig.SettingsConfig -> SettingsViewModel(context)
                MainConfig.WvwMapConfig -> WvwMapViewModel(context, showDialog)
                MainConfig.WvwMatchContestedAreasConfig -> WvwMatchContestedAreasViewModel(context, showDialog)
                MainConfig.WvwMatchStatisticsConfig -> WvwMatchStatisticsViewModel(context, showDialog)
            }
        }
    )

    val splashRouter: Router<SplashConfig, SplashViewModel> = context.createRouter(
        initialStack = { listOf(SplashConfig.NoSplashConfig, SplashConfig.InitializationConfig) },
        serializer = serializer(),
        key = "Splash",
        childFactory = { state, context ->
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

        // Otherwise see if the page can handle the backpress.
        mainRouter.activeChild.instance.onBackPressed() -> true

        // Otherwise if we aren't on the overview page then go back to it.
        mainRouter.activeChild.instance !is WvwMatchOverviewViewModel -> {
            mainRouter.bringToFront(MainConfig.WvwMatchOverviewConfig)
            true
        }

        // Otherwise let the system propagate backing out.
        else -> false
    }
}