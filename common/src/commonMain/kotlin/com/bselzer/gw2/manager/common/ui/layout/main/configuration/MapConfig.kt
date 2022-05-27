package com.bselzer.gw2.manager.common.ui.layout.main.configuration

import com.arkivanov.essenty.parcelable.Parcelize
import com.bselzer.gw2.manager.common.ui.base.Configuration
import com.bselzer.gw2.v2.model.wvw.objective.WvwMapObjectiveId

sealed class MapConfig : Configuration {
    @Parcelize
    data class ObjectiveConfig(val id: WvwMapObjectiveId) : MapConfig()

    @Parcelize
    object ViewerConfig : MapConfig()
}