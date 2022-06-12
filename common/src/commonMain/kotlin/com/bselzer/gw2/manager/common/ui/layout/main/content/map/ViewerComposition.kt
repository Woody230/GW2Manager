package com.bselzer.gw2.manager.common.ui.layout.main.content.map

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.main.content.map.viewer.PlatformGridComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ViewerViewModel
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.background.image.BackgroundImage
import kotlinx.coroutines.launch

class ViewerComposition(model: ViewerViewModel) : ViewModelComposition<ViewerViewModel>(model) {
    @Composable
    override fun ViewerViewModel.Content() {
        // NOTE: Intentionally not using a background in case the device size is large enough that the map doesn't match the full size.

        val scope = rememberCoroutineScope()
        val pinchToZoom = rememberTransformableState { zoomChange, panChange, rotationChange ->
            // Allow the user to change the zoom by pinching the map.
            val change = if (zoomChange > 1) 1 else -1
            scope.launch { changeZoom(change) }
        }

        ConstraintLayout(
            modifier = Modifier.fillMaxSize().transformable(pinchToZoom)
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
        val selected = selectedObjective ?: return
        BackgroundImage(
            modifier = modifier,
            painter = relativeBackgroundPainter,
            presenter = relativeBackgroundPresenter
        ) {
            Column(modifier = Modifier.padding(horizontal = 5.dp)) {
                val textSize = 16.sp
                Text(text = selected.title.localized(), fontSize = textSize, fontWeight = FontWeight.Bold)
                selected.subtitle?.let { subtitle ->
                    Text(text = subtitle.localized(), fontSize = textSize)
                }
            }
        }
    }
}