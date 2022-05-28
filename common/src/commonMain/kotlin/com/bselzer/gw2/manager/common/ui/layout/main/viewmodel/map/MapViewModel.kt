package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map

import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.AppBarAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.SelectedWorldRefreshAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.WorldSelectionAction
import dev.icerock.moko.resources.desc.StringDesc

sealed class MapViewModel(
    context: AppComponentContext,
    showDialog: (DialogConfig) -> Unit
) : ViewModel(context) {
    abstract val title: StringDesc
    open val actions: List<AppBarAction> = listOf(
        SelectedWorldRefreshAction(context.repositories.selectedWorld),
        WorldSelectionAction(showDialog)
    )
}