package com.bselzer.gw2.manager.common.ui.layout.splash.configuration

import com.arkivanov.essenty.parcelable.Parcelize
import com.bselzer.gw2.manager.common.ui.base.Configuration

sealed class SplashConfig : Configuration {
    @Parcelize
    object DefaultConfig : SplashConfig()

    @Parcelize
    object InitializationConfig : SplashConfig()
}