package com.bselzer.gw2.manager.common.ui.layout.main.configuration

import com.arkivanov.essenty.parcelable.Parcelize
import com.bselzer.gw2.manager.common.ui.base.Configuration

sealed class MapConfig : Configuration {
    @Parcelize
    data class ObjectiveConfig(val id: String) : MapConfig()

    @Parcelize
    object ViewerConfig : MapConfig()
}