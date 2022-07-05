package com.bselzer.gw2.manager.common.ui.layout.main.content.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.main.content.map.viewer.PlatformGridComposition
import com.bselzer.gw2.manager.common.ui.layout.main.content.map.viewer.SelectedLabelComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ViewerViewModel

class ViewerComposition(model: ViewerViewModel) : ViewModelComposition<ViewerViewModel>(model) {
    @Composable
    override fun ViewerViewModel.Content(modifier: Modifier) {
        // NOTE: Intentionally not using a background in case the device size is large enough that the map doesn't match the full size.

        ConstraintLayout(
            modifier = Modifier.fillMaxSize().then(modifier)
        ) {
            val (map, selectedObjective) = createRefs()
            PlatformGridComposition(model).Content(
                modifier = Modifier.constrainAs(map) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            // Overlay the selected objective over everything else on the map.
            SelectedObjectiveLabel(
                modifier = Modifier.constrainAs(selectedObjective) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
            )
        }
    }

    /**
     * Lays out general information about the objective the user clicked on in a pop-up label.
     */
    @Composable
    private fun ViewerViewModel.SelectedObjectiveLabel(modifier: Modifier) {
        val selected = selectedLabel ?: return
        SelectedLabelComposition(selected).Content(modifier = modifier)
    }
}