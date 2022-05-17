package com.bselzer.gw2.manager.common.ui.layout.main.configuration

import com.arkivanov.essenty.parcelable.Parcelize
import com.bselzer.gw2.manager.common.ui.base.Configuration

sealed class MainConfig : Configuration {
    @Parcelize
    object AboutConfig : MainConfig()

    @Parcelize
    object CacheConfig : MainConfig()

    @Parcelize
    object LicenseConfig : MainConfig()

    @Parcelize
    object ModuleConfig : MainConfig()

    @Parcelize
    object SettingsConfig : MainConfig()

    @Parcelize
    object WvwMapConfig : MainConfig()

    @Parcelize
    object WvwMatchConfig : MainConfig()
}