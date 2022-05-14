package com.bselzer.gw2.manager.common.ui.base

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.router.RouterState
import com.arkivanov.decompose.value.Value

interface ChildComposition<Config : Configuration, Model : ViewModel> {
    /**
     * Lays out the content using the [RouterState].
     */
    @Composable
    fun Content(state: Value<RouterState<Config, Model>>)
}