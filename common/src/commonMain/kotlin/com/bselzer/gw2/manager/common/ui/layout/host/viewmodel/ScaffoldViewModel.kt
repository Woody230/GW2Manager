package com.bselzer.gw2.manager.common.ui.layout.host.viewmodel

import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel

class ScaffoldViewModel(context: AppComponentContext) : ViewModel(context) {
    val drawer: DrawerViewModel = DrawerViewModel(context)
}