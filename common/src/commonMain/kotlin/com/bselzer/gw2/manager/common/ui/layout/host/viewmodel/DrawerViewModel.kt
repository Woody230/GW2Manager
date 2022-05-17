package com.bselzer.gw2.manager.common.ui.layout.host.viewmodel

import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.host.model.drawer.DrawerComponent
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MainConfig
import com.bselzer.ktx.resource.Resources
import dev.icerock.moko.resources.desc.desc

class DrawerViewModel(context: AppComponentContext) : ViewModel(context) {
    private val wvwMap = DrawerComponent(
        icon = Gw2Resources.images.gw2_rank_dolyak,
        description = Gw2Resources.strings.wvw_map.desc(),
        configuration = MainConfig.WvwMapConfig
    )

    private val wvwMatch = DrawerComponent(
        icon = Gw2Resources.images.gw2_rank_dolyak,
        description = Gw2Resources.strings.wvw_match.desc(),
        configuration = MainConfig.WvwMatchConfig
    )

    private val settings = DrawerComponent(
        icon = Gw2Resources.images.ic_settings,
        description = Resources.strings.settings.desc(),
        configuration = MainConfig.SettingsConfig
    )

    private val cache = DrawerComponent(
        icon = Gw2Resources.images.ic_cached,
        description = Resources.strings.cache.desc(),
        configuration = MainConfig.CacheConfig
    )

    private val license = DrawerComponent(
        icon = Gw2Resources.images.ic_policy,
        description = Resources.strings.licenses.desc(),
        configuration = MainConfig.LicenseConfig
    )

    private val about = DrawerComponent(
        icon = Gw2Resources.images.ic_info,
        description = Resources.strings.about_app.desc(),
        configuration = MainConfig.AboutConfig
    )

    val icons: List<List<DrawerComponent>> = listOf(
        listOf(wvwMap, wvwMatch),
        listOf(settings, cache),
        listOf(license, about)
    )
}