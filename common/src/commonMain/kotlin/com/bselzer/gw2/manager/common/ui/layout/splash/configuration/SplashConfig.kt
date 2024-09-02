package com.bselzer.gw2.manager.common.ui.layout.splash.configuration

import com.bselzer.gw2.manager.common.ui.base.Configuration
import kotlinx.serialization.Serializable

@Serializable
sealed class SplashConfig : Configuration {
    @Serializable
    object NoSplashConfig : SplashConfig()

    @Serializable
    object InitializationConfig : SplashConfig()
}