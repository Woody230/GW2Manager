package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.layout.common.AbsoluteBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.content.ContestedAreasComposition
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.viewmodel.ContestedAreasViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMatchContestedAreasViewModel
import com.bselzer.gw2.v2.model.extension.wvw.count.ObjectiveOwnerCount

class WvwMatchContestedAreasComposition(
    model: WvwMatchContestedAreasViewModel
) : WvwMatchComposition<WvwMatchContestedAreasViewModel, ObjectiveOwnerCount>(model), ContestedAreasComposition<ContestedAreasViewModel> {
    @Composable
    override fun WvwMatchContestedAreasViewModel.Content() = AbsoluteBackgroundImage(
        modifier = Modifier.fillMaxSize()
    ) {
        BorderlandsContent()
    }

    @Composable
    override fun ObjectiveOwnerCount.Content() = with(model) { toContestedAreasModel().ContestedAreasContent() }
}