package com.bselzer.gw2.manager.common.ui.base

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

abstract class ViewModelComposition<Model : ViewModel>(model: Model) : ModelComposition<Model>(model) {
    protected val padding: Dp = 25.dp
    protected val paddingValues: PaddingValues = PaddingValues(all = padding)
}