package com.bselzer.gw2.manager.common.ui.layout.host.viewmodel

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.host.model.drawer.DrawerComponent
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MainConfig
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DrawerViewModel(
    context: AppComponentContext,
) : ViewModel(context) {
    val wvwTitle: StringDesc = AppResources.strings.wvw.desc()

    val wvwMap = DrawerComponent(
        icon = AppResources.images.gw2_rank_dolyak,
        description = AppResources.strings.wvw_map.desc(),
        configuration = MainConfig.WvwMapConfig
    )

    val wvwMatch = DrawerComponent(
        icon = AppResources.images.gw2_rank_dolyak,
        description = AppResources.strings.wvw_match.desc(),
        configuration = MainConfig.WvwMatchConfig
    )

    val settings = DrawerComponent(
        icon = KtxResources.images.ic_settings,
        description = KtxResources.strings.settings.desc(),
        configuration = MainConfig.SettingsConfig
    )

    val cache = DrawerComponent(
        icon = KtxResources.images.ic_cached,
        description = KtxResources.strings.cache.desc(),
        configuration = MainConfig.CacheConfig
    )

    val license = DrawerComponent(
        icon = KtxResources.images.ic_policy,
        description = KtxResources.strings.licenses.desc(),
        configuration = MainConfig.LicenseConfig
    )

    val about = DrawerComponent(
        icon = KtxResources.images.ic_info,
        description = KtxResources.strings.about.desc(),
        configuration = MainConfig.AboutConfig
    )

    val confirmStateChange: (DrawerValue) -> Boolean = { true }
    val state = DrawerState(initialValue = DrawerValue.Closed, confirmStateChange)

    /**
     * Opens the drawer.
     */
    fun CoroutineScope.open() = launch { state.open() }

    /**
     * Closes the drawer.
     */
    fun CoroutineScope.close() = launch { state.close() }
}