package com.bselzer.gw2.manager.common.ui.layout.contestedarea.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.viewmodel.ContestedAreasViewModel

interface ContestedAreasComposition<Model : ContestedAreasViewModel> {
    @Composable
    fun Model.ContestedAreasContent(
        modifier: Modifier = Modifier
    ) = ConstraintLayout(
        modifier = modifier,
    ) {
        ConstrainedComposition(model = this@ContestedAreasContent, constraintScope = this).Content()
    }
}