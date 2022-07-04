package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import com.bselzer.gw2.manager.common.repository.data.specialized.SelectedWorldData
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.custom.chart.viewmodel.ChartViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.contestedarea.viewmodel.ContestedAreasViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.WorldViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.statistics.viewmodel.OwnerOverviewsViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.SelectedWorldRefreshAction.Companion.refreshAction
import com.bselzer.gw2.v2.model.extension.wvw.count.contestedarea.ContestedAreas
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class WvwMatchOverviewViewModel(
    context: AppComponentContext
) : MainViewModel(context),
    SelectedWorldData by context.repositories.selectedWorld,
    OwnerOverviewsViewModel,
    ContestedAreasViewModel {
    override val title: StringDesc = KtxResources.strings.overview.desc()

    override val actions
        get() = listOf(refreshAction())

    val vpChart: ChartViewModel
        get() = ChartViewModel(
            context = this,
            data = count.victoryPoints,
        )

    val pptChart: ChartViewModel
        get() = ChartViewModel(
            context = this,
            data = count.pointsPerTick
        )

    override val contestedAreas: ContestedAreas
        get() = count.contestedAreas

    val selectedWorld: WorldViewModel
        get() = WorldViewModel(context = this)
}