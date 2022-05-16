package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.router.Router
import com.arkivanov.decompose.router.bringToFront
import com.bselzer.gw2.manager.common.ui.base.RouterComposition
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.DialogViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MainConfig
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.MainViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.ModuleViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.SettingsViewModel

class MainComposition(
    router: Router<MainConfig, MainViewModel>,
    private val dialog: Router<DialogConfig, DialogViewModel>,
) : RouterComposition<MainConfig, MainViewModel>(router) {
    @Composable
    override fun MainViewModel.Content() = when (this) {
        // TODO scaffold, drawer, top app bar
        is ModuleViewModel -> ModuleComposition { config ->
            dialog.bringToFront(config)
        }.Content(this)
        is SettingsViewModel -> {
            /* TODO settings */
        }
    }
}