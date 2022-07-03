package com.bselzer.gw2.manager.common.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

abstract class ModelComposition<Model>(protected val model: Model) {
    /**
     * Lays out the content using the [Model].
     */
    @Composable
    fun Content(modifier: Modifier = Modifier) = model.Content(modifier)

    /**
     * Lays out the content using the [Model].
     */
    @Composable
    protected abstract fun Model.Content(modifier: Modifier)
}