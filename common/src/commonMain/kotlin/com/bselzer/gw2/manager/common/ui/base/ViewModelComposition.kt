package com.bselzer.gw2.manager.common.ui.base

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

abstract class ViewModelComposition<Model : ViewModel>(protected val model: Model) {
    /**
     * Lays out the content using the [ViewModel].
     */
    @Composable
    fun Content(modifier: Modifier = Modifier) = model.Content(modifier)

    /**
     * Lays out the content using the [ViewModel].
     */
    @Composable
    protected abstract fun Model.Content(modifier: Modifier)

    protected val padding: Dp = 25.dp
    protected val paddingValues: PaddingValues = PaddingValues(all = padding)
}