package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import com.bselzer.gw2.manager.common.repository.data.specialized.SelectedWorldData
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.AppBarAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.SelectedWorldRefreshAction.Companion.refreshAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.WorldSelectionAction
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.match.StatisticsViewModel
import com.bselzer.gw2.v2.resource.Gw2Resources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class WvwMatchViewModel(
    context: AppComponentContext,
    private val showDialog: (DialogConfig) -> Unit
) : MainViewModel(context), SelectedWorldData by context.repositories.selectedWorld {
    override val title: StringDesc = Gw2Resources.strings.match.desc()

    override val actions: List<AppBarAction>
        get() = listOf(
            refreshAction(),
            WorldSelectionAction(showDialog)
        )

    val statistics: StatisticsViewModel?
        get() = match?.let { match -> StatisticsViewModel(this, match) }
}