package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.Action
import dev.icerock.moko.resources.desc.StringDesc

sealed class MainViewModel(context: AppComponentContext) : ViewModel(context) {
    abstract val title: StringDesc
    open val actions: List<Action> = emptyList()

    /**
     * Handles back navigation between the routers.
     *
     * @return whether the back press is handled
     */
    open fun onBackPressed(): Boolean = false
}