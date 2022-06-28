package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.runtime.Composable
import com.bselzer.gw2.manager.common.ui.base.RouterComposition
import com.bselzer.gw2.manager.common.ui.layout.host.content.LocalMainRouter
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MainConfig
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.*
import com.bselzer.ktx.compose.ui.layout.iconbutton.IconButtonInteractor
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor

class MainComposition : RouterComposition<MainConfig, MainViewModel>(
    router = { LocalMainRouter.current }
) {
    /**
     * lays out the content for the current [MainViewModel].
     */
    @Composable
    override fun MainViewModel.Content() = composition().Content()

    /**
     * Gets the top app bar title for the current [MainViewModel].
     */
    @Composable
    fun title(): TextInteractor = rememberActiveChild().instance.composition().title

    /**
     * Gets the top app bar actions for the current [MainViewModel].
     */
    @Composable
    fun actions(): List<IconButtonInteractor> = rememberActiveChild().instance.composition().actions()

    /**
     * Creates the [MainChildComposition] for the current [MainViewModel].
     */
    private fun MainViewModel.composition(): MainChildComposition<*> = when (this) {
        is AboutViewModel -> AboutComposition(this)
        is CacheViewModel -> CacheComposition(this)
        is LicenseViewModel -> LicenseComposition(this)
        is SettingsViewModel -> SettingsComposition(this)
        is WvwMapViewModel -> WvwMapComposition(this)
        is WvwMatchContestedAreasViewModel -> WvwMatchContestedAreasComposition(this)
        is WvwMatchOverviewViewModel -> WvwMatchOverviewComposition(this)
        is WvwMatchStatisticsViewModel -> WvwMatchStatisticsComposition(this)
    }
}