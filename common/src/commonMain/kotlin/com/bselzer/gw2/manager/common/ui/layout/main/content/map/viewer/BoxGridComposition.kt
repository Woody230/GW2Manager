package com.bselzer.gw2.manager.common.ui.layout.main.content.map.viewer

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.layout.main.model.map.viewer.Bloodlust
import com.bselzer.gw2.manager.common.ui.layout.main.model.map.viewer.ObjectiveIcon
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ViewerViewModel
import com.bselzer.ktx.compose.ui.unit.toDp
import com.bselzer.ktx.compose.ui.unit.toPx

/**
 * A composition for laying out the grid of tiles using a box to hold the scrolling capabilities.
 */
class BoxGridComposition(model: ViewerViewModel) : GridComposition(model) {
    @Composable
    override fun ViewerViewModel.Content(modifier: Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .horizontalScroll(rememberSaveable(saver = ScrollState.Saver) { horizontalScroll })
                .verticalScroll(rememberSaveable(saver = ScrollState.Saver) { verticalScroll })
        ) {
            MapGrid()

            if (grid.rows.isNotEmpty()) {
                objectiveIcons.forEach { objective -> objective.Objective() }
                bloodlustIcons.forEach { bloodlust -> bloodlust.Bloodlust() }
            }
        }

        scrollToRegion()

        LaunchedEffect(true) {
            refreshGrid = true
        }
    }

    @Composable
    private fun Bloodlust.Bloodlust() {
        // Displace the coordinates so that it aligns with the center of the image.
        val (width, height) = bloodlustSize
        val displacedX = position.x - width.toPx() / 2
        val displacedY = position.y - height.toPx() / 2
        Bloodlust(
            modifier = Modifier.absoluteOffset(
                x = displacedX.toDp(),
                y = displacedY.toDp()
            ),
        )
    }

    @Composable
    private fun ObjectiveIcon.Objective() {
        // Displace the coordinates so that it aligns with the center of the image.
        val (width, height) = objectiveSize
        val displacedX = position.x - width.toPx() / 2
        val displacedY = position.y - height.toPx() / 2
        Objective(
            modifier = Modifier.absoluteOffset(
                displacedX.toDp(),
                displacedY.toDp()
            )
        )
    }

    /**
     * Lays out the map represented by a tiled grid.
     */
    @Composable
    private fun ViewerViewModel.MapGrid() = Column(
        modifier = Modifier.fillMaxSize()
    ) {
        grid.rows.forEach { row ->
            Row {
                row.forEach { tile -> tile.MapTile() }
            }
        }
    }
}