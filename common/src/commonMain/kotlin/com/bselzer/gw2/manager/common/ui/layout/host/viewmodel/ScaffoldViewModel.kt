package com.bselzer.gw2.manager.common.ui.layout.host.viewmodel

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel

class ScaffoldViewModel(context: AppComponentContext) : ViewModel(context) {
    val drawer = DrawerViewModel(context)
    val state = ScaffoldState(drawerState = drawer.state, snackbarHostState = SnackbarHostState())
}