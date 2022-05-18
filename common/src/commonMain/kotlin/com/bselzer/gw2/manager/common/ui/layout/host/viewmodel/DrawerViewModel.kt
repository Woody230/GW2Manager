package com.bselzer.gw2.manager.common.ui.layout.host.viewmodel

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.host.model.drawer.DrawerComponent
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MainConfig
import com.bselzer.ktx.resource.Resources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DrawerViewModel(
    context: AppComponentContext,
) : ViewModel(context) {
    val wvwTitle: StringDesc = Gw2Resources.strings.wvw.desc()

    val wvwMap = DrawerComponent(
        icon = Gw2Resources.images.gw2_rank_dolyak,
        description = Gw2Resources.strings.wvw_map.desc(),
        configuration = MainConfig.WvwMapConfig
    )

    val wvwMatch = DrawerComponent(
        icon = Gw2Resources.images.gw2_rank_dolyak,
        description = Gw2Resources.strings.wvw_match.desc(),
        configuration = MainConfig.WvwMatchConfig
    )

    val settings = DrawerComponent(
        icon = Gw2Resources.images.ic_settings,
        description = Resources.strings.settings.desc(),
        configuration = MainConfig.SettingsConfig
    )

    val cache = DrawerComponent(
        icon = Gw2Resources.images.ic_cached,
        description = Resources.strings.cache.desc(),
        configuration = MainConfig.CacheConfig
    )

    val license = DrawerComponent(
        icon = Gw2Resources.images.ic_policy,
        description = Resources.strings.licenses.desc(),
        configuration = MainConfig.LicenseConfig
    )

    val about = DrawerComponent(
        icon = Gw2Resources.images.ic_info,
        description = Resources.strings.about_app.desc(),
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