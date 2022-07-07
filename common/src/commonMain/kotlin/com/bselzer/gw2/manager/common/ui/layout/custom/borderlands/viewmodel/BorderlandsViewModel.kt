package com.bselzer.gw2.manager.common.ui.layout.custom.borderlands.viewmodel

import com.bselzer.gw2.manager.common.dependency.ViewModelDependencies
import com.bselzer.gw2.manager.common.repository.data.specialized.MatchData
import com.bselzer.gw2.manager.common.ui.layout.custom.borderlands.model.DataSet
import com.bselzer.gw2.v2.model.enumeration.WvwMapType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.extension.wvw.owner
import com.bselzer.gw2.v2.model.wvw.map.WvwMap
import com.bselzer.gw2.v2.resource.strings.stringDesc
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.desc

interface BorderlandsViewModel<T> : ViewModelDependencies, MatchData {
    /**
     * The [DataSet]s for the [overviewData] and [borderlandData].
     */
    val dataSets: List<DataSet<T>>
        get() = listOf(overviewDataSet) + borderlandsDataSets

    /**
     * The data to use when unable to locate a [DataSet] from [dataSets].
     */
    val defaultData: T

    /**
     * The [DataSet] for the [defaultData].
     */
    val defaultDataSet: DataSet<T>
        get() = DataSet(
            title = "".desc(),
            color = WvwObjectiveOwner.NEUTRAL.color(),
            data = defaultData
        )

    /**
     * The data to use for the combination of all [borderlandData].
     */
    fun overviewData(): T

    /**
     * The [DataSet] for the [overviewData].
     */
    private val overviewDataSet: DataSet<T>
        get() = DataSet(
            title = KtxResources.strings.overview.desc(),
            color = null,
            data = overviewData()
        )

    /**
     * The data to use for a specific borderland.
     */
    fun borderlandData(map: WvwMap): T

    /**
     * The type of [WvwMap] mapped to the associated [borderlandData].
     * Only maps with a type in the [mapTypes] are considered.
     */
    @Suppress("UNCHECKED_CAST")
    private val borderlandsData: Map<WvwMapType, T>
        get() = maps.mapValues { (_, map) -> borderlandData(map) }

    /**
     * The [DataSet]s for the [borderlandsData].
     */
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