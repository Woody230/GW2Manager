package com.bselzer.gw2.manager.common.ui.layout.main.configuration

import com.arkivanov.essenty.parcelable.Parcelize
import com.bselzer.gw2.manager.common.ui.base.Configuration

sealed class MainConfig : Configuration {
    @Parcelize
    object ModuleConfig : MainConfig()

    @Parcelize
    object SettingsConfig : MainConfig()
}