package com.bselzer.gw2.manager.common.ui.layout.main.content.map.viewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.router.bringToFront
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.Content
import com.bselzer.gw2.manager.common.ui.layout.common.ProgressIndication
import com.bselzer.gw2.manager.common.ui.layout.custom.indicator.content.DetailedIconComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.indicator.viewmodel.DetailedIconViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MapConfig
import com.bselzer.gw2.manager.common.ui.layout.main.content.map.LocalMapRouter
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.BloodlustViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.MapLabelViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ViewerViewModel
import com.bselzer.gw2.v2.tile.model.response.Tile
import com.bselzer.ktx.compose.image.ui.layout.asImageBitmap
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.unit.toDp

expect fun PlatformGridComposition(model: ViewerViewModel): GridComposition

/**
 * The composition for laying out the grid of tiles.
 */
abstract class GridComposition(model: ViewerViewModel) : ViewModelComposition<ViewerViewModel>(model) {
    protected companion object {
        val objectiveSize: DpSize = DpSize(32.dp, 32.dp)
        val bloodlustSize: DpSize = DpSize(32.dp, 32.dp)
    }

    /**
     * Lays out an individual tile within the grid.
     */
    @Composable
    protected fun Tile.MapTile() {
        // Need to specify non-zero width/height on the default bitmap.
        val bitmap = if (content.isNotEmpty()) content.asImageBitmap() else ImageBitmap(1, 1)
        Image(
            painter = BitmapPainter(bitmap),
            contentDescription = AppResources.strings.wvw_tile.localized(),
            modifier = Modifier
                .size(size.width.toDp(), size.height.toDp())
                .clickable(
                    // Disable the ripple so that the illusion of a contiguous map is not broken.
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // Clear the objective pop-up.
                    model.selected.value = null
                }
        )
    }

    /**
     * Lays out the image indicating the owner of bloodlust.
     */
    @Composable
    protected fun BloodlustViewModel.Bloodlust(modifier: Modifier) = image.Content(
        progressIndication = ProgressIndication.DISABLED,
        modifier = modifier,
        size = bloodlustSize
    )

    /**
     * Lays out the individual objective on the map.
     */
    @Composable
    protected fun DetailedIconViewModel.Objective(modifier: Modifier) {
        val mapRouter = LocalMapRouter.current
        DetailedIconComposition(
            model = this,
            onLongClick = {
                // Swap pages to display all of the information instead of the limited information that normally comes with the pop-up.
                val config = MapConfig.ObjectiveConfig(objective.id.value)
                mapRouter.bringToFront(config)
            },
            onClick = {
                // Set the selected objective for displaying the pop-up.
                model.selected.value = objective
            }
        ).Content(modifier = modifier)
    }

    /**
     * Lays out the label for displaying the map owner or name.
     */
    @Composable
    protected fun MapLabelViewModel.Label(modifier: Modifier) = MapLabelComposition(this).Content(modifier = modifier)
}