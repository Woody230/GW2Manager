package com.bselzer.gw2.manager.common.ui.layout.main.content.map.viewer

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ViewerViewModel

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
                objectiveIcons.forEach { objective -> Objective(objective) }
                bloodlusts.forEach { bloodlust -> bloodlust.Bloodlust() }
            }
        }

        scrollToRegion()
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