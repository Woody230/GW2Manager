package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import com.bselzer.gw2.manager.common.dependency.ViewModelDependencies
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.chart.viewmodel.ChartViewModel
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.viewmodel.ContestedAreasViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.v2.model.extension.wvw.count.ObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.count.WvwMapObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.count.WvwMatchObjectiveOwnerCount
import com.bselzer.gw2.v2.model.extension.wvw.count.contestedarea.ContestedAreas
import com.bselzer.gw2.v2.model.wvw.map.WvwMap
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.resource.Gw2Resources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class WvwMatchContestedAreasViewModel(
    context: AppComponentContext,
    showDialog: (DialogConfig) -> Unit
) : WvwMatchBorderlandsViewModel<ObjectiveOwnerCount>(context, showDialog) {
    override val title: StringDesc = Gw2Resources.strings.contested_areas.desc()

    override val defaultData: ObjectiveOwnerCount = ObjectiveOwnerCount

    override val overviewData: (WvwMatch) -> ObjectiveOwnerCount = { match -> WvwMatchObjectiveOwnerCount(match) }

    override val borderlandData: (WvwMap) -> ObjectiveOwnerCount = { map -> WvwMapObjectiveOwnerCount(map) }

    fun ObjectiveOwnerCount.toContestedAreasModel(): ContestedAreasViewModel = object : ContestedAreasViewModel,
        ViewModelDependencies by this@WvwMatchContestedAreasViewModel {
        override val contestedAreas: ContestedAreas = this@toContestedAreasModel.contestedAreas
    }

    val charts: List<ChartViewModel>
        get() = listOf(overviewChart) + borderlandsCharts

    private val overviewChart: ChartViewModel
        get() = ChartViewModel(
            context = this,
            data = overviewData(match).pointsPerTick
        )

    private val borderlandsCharts: List<ChartViewModel>
        get() = maps.map { (_, map) ->
            ChartViewModel(
                context = this,
                data = borderlandData(map).pointsPerTick
            )
        }
}