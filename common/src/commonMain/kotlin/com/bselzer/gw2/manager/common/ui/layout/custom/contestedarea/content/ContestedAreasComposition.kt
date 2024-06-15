package com.bselzer.gw2.manager.common.ui.layout.custom.contestedarea.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import com.bselzer.gw2.manager.common.ui.layout.custom.contestedarea.viewmodel.ContestedAreasViewModel

interface ContestedAreasComposition {
    @Composable
    fun ContestedAreasViewModel.ContestedAreas(
        modifier: Modifier
    ) = ConstraintLayout(
        modifier = modifier,
    ) {
        ConstrainedComposition(model = this@ContestedAreas, constraintScope = this).Content()
    }
}