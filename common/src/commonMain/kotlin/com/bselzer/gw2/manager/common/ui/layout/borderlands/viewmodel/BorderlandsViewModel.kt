package com.bselzer.gw2.manager.common.ui.layout.borderlands.viewmodel

import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.borderlands.model.DataSet
import com.bselzer.gw2.v2.model.enumeration.WvwMapType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.extension.decodeOrNull
import com.bselzer.gw2.v2.model.extension.wvw.owner
import com.bselzer.gw2.v2.model.wvw.map.WvwMap
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.resource.strings.stringDesc
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.desc

abstract class BorderlandsViewModel<T>(
    context: AppComponentContext,
    private val match: WvwMatch
) : ViewModel(context) {
    val dataSets: List<DataSet<T>>
        get() = listOf(overviewDataSet) + borderlandsDataSets

    protected abstract val defaultData: T

    val defaultDataSet: DataSet<T>
        get() = DataSet(
            title = "".desc(),
            color = WvwObjectiveOwner.NEUTRAL.color(),
            data = defaultData
        )

    protected abstract val overviewData: (WvwMatch) -> T

    private val overviewDataSet: DataSet<T>
        get() = DataSet(
            title = KtxResources.strings.overview.desc(),
            color = null,
            data = overviewData(match)
        )

    protected abstract val borderlandData: (WvwMap) -> T

    @Suppress("UNCHECKED_CAST")
    private val borderlandsData: Map<WvwMapType, T>
        get() {
            val data = match.maps.associate { map -> map.type.decodeOrNull() to borderlandData(map) }.filterKeys { type -> type != null && mapTypes.contains(type) }
            return data as Map<WvwMapType, T>
        }

    private val borderlandsDataSets: List<DataSet<T>>
        get() {
            val sorted = borderlandsData.entries.sortedBy { (type, _) -> mapTypes.indexOf(type) }
            return sorted.map { (type, data) ->
                // TODO use linked worlds instead for title? or main world?
                DataSet(
                    title = type.stringDesc(),
                    color = type.owner().color(),
                    data = data
                )
            }
        }
}