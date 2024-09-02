package com.bselzer.gw2.manager.common.ui.layout.main.configuration

import com.bselzer.gw2.manager.common.ui.base.Configuration
import kotlinx.serialization.Serializable

@Serializable
sealed class MainConfig : Configuration {
    @Serializable
    object AboutConfig : MainConfig()

    @Serializable
    object CacheConfig : MainConfig()

    @Serializable
    object LicenseConfig : MainConfig()

    @Serializable
    object WvwMatchOverviewConfig : MainConfig()

    @Serializable
    object SettingsConfig : MainConfig()

    @Serializable
    object WvwMapConfig : MainConfig()

    @Serializable
    object WvwMatchContestedAreasConfig : MainConfig()

    @Serializable
    object WvwMatchStatisticsConfig : MainConfig()
}