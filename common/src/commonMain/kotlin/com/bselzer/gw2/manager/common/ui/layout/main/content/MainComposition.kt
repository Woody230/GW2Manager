package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.router.Router
import com.bselzer.gw2.manager.common.ui.base.RouterComposition
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MainConfig
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.MainViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.ModuleViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.SettingsViewModel

class MainComposition(
    router: Router<MainConfig, MainViewModel>
) : RouterComposition<MainConfig, MainViewModel>(router) {
    @Composable
    override fun MainViewModel.Content() = when (this) {
        // TODO scaffold, drawer, top app bar
        is ModuleViewModel -> ModuleComposition().Content(this)
        is SettingsViewModel -> { /* TODO settings */
        }
    }
}