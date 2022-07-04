package com.bselzer.gw2.manager.common.ui.layout.custom.contestedarea.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import com.bselzer.gw2.manager.common.ui.layout.custom.contestedarea.viewmodel.ContestedAreasViewModel

interface ContestedAreasComposition {
    @Composable
    fun ContestedAreasViewModel.ContestedAreasContent(
        modifier: Modifier = Modifier
    ) = ConstraintLayout(
        modifier = modifier,
    ) {
        ConstrainedComposition(model = this@ContestedAreasContent, constraintScope = this).Content()
    }
}