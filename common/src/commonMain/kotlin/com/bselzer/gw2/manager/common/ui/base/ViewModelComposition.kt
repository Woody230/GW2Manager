package com.bselzer.gw2.manager.common.ui.base

import androidx.compose.runtime.Composable

abstract class ViewModelComposition<Model : ViewModel> {
    /**
     * Lays out the content using the [ViewModel].
     */
    @Composable
    abstract fun Content(model: Model)
}