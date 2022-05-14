package com.bselzer.gw2.manager.common.ui.base

import androidx.compose.runtime.Composable

interface Composition<Model : ViewModel> {
    /**
     * Lays out the content using the [ViewModel].
     */
    @Composable
    fun Content(model: Model)
}