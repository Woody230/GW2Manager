package com.bselzer.gw2.manager.common.ui.layout.main.configuration

import com.bselzer.gw2.manager.common.ui.base.Configuration
import kotlinx.serialization.Serializable

@Serializable
sealed class MapConfig : Configuration {
    @Serializable
    data class ObjectiveConfig(val id: String) : MapConfig()

    @Serializable
    object ViewerConfig : MapConfig()
}