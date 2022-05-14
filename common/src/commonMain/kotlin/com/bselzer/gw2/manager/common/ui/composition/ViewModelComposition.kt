package com.bselzer.gw2.manager.common.ui.composition

import androidx.compose.runtime.Composable
import com.bselzer.gw2.manager.common.ui.base.ViewModel

abstract class ViewModelComposition<Model : ViewModel> {
    /**
     * Lays out the content using the [ViewModel].
     */
    @Composable
    abstract fun Content(model: Model)
}