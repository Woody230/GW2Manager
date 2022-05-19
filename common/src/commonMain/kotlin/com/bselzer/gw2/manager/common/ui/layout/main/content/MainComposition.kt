package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.runtime.Composable
import com.bselzer.gw2.manager.common.ui.base.RouterComposition
import com.bselzer.gw2.manager.common.ui.layout.host.content.LocalMainRouter
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MainConfig
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.*

class MainComposition : RouterComposition<MainConfig, MainViewModel>(
    router = { LocalMainRouter.current }
) {
    @Composable
    override fun MainViewModel.Content() = when (this) {
        is AboutViewModel -> {}
        is CacheViewModel -> CacheComposition().Content(this)
        is LicenseViewModel -> LicenseComposition().Content(this)
        is ModuleViewModel -> ModuleComposition().Content(this)
        is SettingsViewModel -> {}
        is WvwMapViewModel -> {}
        is WvwMatchViewModel -> {}
    }
}