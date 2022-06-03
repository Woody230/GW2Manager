package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.bselzer.gw2.manager.common.ui.layout.main.content.map.LocalMapRouter
import com.bselzer.gw2.manager.common.ui.layout.main.content.map.MapComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMapViewModel
import com.bselzer.ktx.compose.ui.layout.iconbutton.IconButtonInteractor

class WvwMapComposition(model: WvwMapViewModel) : MainChildComposition<WvwMapViewModel>(model) {
    @Composable
    override fun WvwMapViewModel.Content() = CompositionLocalProvider(
        LocalMapRouter provides router
    ) {
        MapComposition().Content()
    }

    override val actions: @Composable () -> List<IconButtonInteractor> = {
        val child = model.router.state.subscribeAsState().value.activeChild.instance
        child.actions.interactors()
    }
}