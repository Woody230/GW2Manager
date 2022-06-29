package com.bselzer.gw2.manager.common.ui.layout.chart.viewmodel

import com.bselzer.gw2.manager.common.repository.data.specialized.SelectedWorldData
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.chart.model.ChartData
import com.bselzer.gw2.manager.common.ui.layout.chart.model.ChartDataSet
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.ImageDesc

class ChartDataViewModel(
    context: AppComponentContext,
    private val data: Map<out WvwObjectiveOwner?, Int>?,
    private val title: StringDesc,
    private val icon: ImageDesc,
) : ViewModel(context), SelectedWorldData by context.repositories.selectedWorld {
    val dataSet: ChartDataSet
        get() = ChartDataSet(
            title = title,
            icon = icon,
            data = chartData
        )

    private val chartData: Collection<ChartData>
        get() = owners.map { owner ->
            val amount = data?.get(owner) ?: 0
            ChartData(
                color = owner.color(),
                data = amount.toString().desc(),
                owner = displayableLinkedWorlds(owner)
            )
        }
}