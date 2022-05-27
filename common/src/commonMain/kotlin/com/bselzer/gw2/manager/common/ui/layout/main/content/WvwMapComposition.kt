package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.bselzer.gw2.manager.common.ui.layout.main.content.map.LocalMapRouter
import com.bselzer.gw2.manager.common.ui.layout.main.content.map.MapComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMapViewModel

class WvwMapComposition(model: WvwMapViewModel) : MainChildComposition<WvwMapViewModel>(model) {
    @Composable
    override fun WvwMapViewModel.Content() = CompositionLocalProvider(
        LocalMapRouter provides router
    ) {
        MapComposition().Content()
    }
}