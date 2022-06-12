package com.bselzer.gw2.manager.common.ui.layout.main.content.map.viewer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.arkivanov.decompose.router.bringToFront
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.layout.image.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.image.Content
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MapConfig
import com.bselzer.gw2.manager.common.ui.layout.main.content.map.LocalMapRouter
import com.bselzer.gw2.manager.common.ui.layout.main.model.map.viewer.*
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ViewerViewModel
import com.bselzer.gw2.v2.tile.model.response.Tile
import com.bselzer.ktx.compose.image.ui.layout.asImageBitmap
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.unit.toDp
import com.bselzer.ktx.datetime.format.minuteFormat
import kotlin.time.Duration

expect fun PlatformGridComposition(model: ViewerViewModel): GridComposition

/**
 * The composition for laying out the grid of tiles.
 */
abstract class GridComposition(protected val model: ViewerViewModel) {
    @Composable
    fun Content(modifier: Modifier) = model.Content(modifier)

    @Composable
    protected abstract fun ViewerViewModel.Content(modifier: Modifier)

    protected val objectiveSize: IntSize = IntSize(64, 64)
    protected val bloodlustSize: IntSize = objectiveSize

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
                .size(width.toDp(), height.toDp())
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
    protected fun Bloodlust.Bloodlust(modifier: Modifier) = AsyncImage(
        image = link,
        width = bloodlustSize.width,
        height = bloodlustSize.height,
        color = color,
        description = description
    ).Content(modifier)

    /**
     * Lays out the individual objective on the map.
     */
    @Composable
    protected fun ObjectiveIcon.Objective(modifier: Modifier) = ConstraintLayout(
        modifier = Modifier.wrapContentSize().then(modifier)
    ) {
        val (icon, timer, upgradeIndicator, claimIndicator, waypointIndicator) = createRefs()

        // Overlay the objective image onto the map image.
        Image(
            width = objectiveSize.width,
            height = objectiveSize.height,
            modifier = Modifier.constrainAs(icon) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )

        progression.Progression(
            modifier = Modifier.constrainAs(upgradeIndicator) {
                // Display the indicator in the top center of the objective icon.
                top.linkTo(icon.top)
                start.linkTo(icon.start)
                end.linkTo(icon.end)
            },
        )

        claim.Claim(
            modifier = Modifier.constrainAs(claimIndicator) {
                // Display the indicator in the bottom right of the objective icon.
                bottom.linkTo(icon.bottom)
                end.linkTo(icon.end)
            }
        )

        waypoint.Waypoint(
            modifier = Modifier.constrainAs(waypointIndicator) {
                // Display the indicator in the bottom left of the objective icon.
                bottom.linkTo(icon.bottom)
                start.linkTo(icon.start)
            },
        )

        immunity.ImmunityTimer(
            modifier = Modifier.constrainAs(timer) {
                // Display the timer underneath the objective icon.
                top.linkTo(icon.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }

    /**
     * Lays out the indicators for the objective's guild if it has been claimed.
     */
    @Composable
    protected fun ObjectiveClaim.Claim(modifier: Modifier) = AsyncImage(
        enabled = enabled,
        image = link,
        description = description,
        width = 32,
        height = 32,
    ).Content(modifier = modifier)

    /**
     * Lays out the indicators for the objective's upgrade progression level.
     */
    @Composable
    protected fun ObjectiveProgression.Progression(modifier: Modifier) = AsyncImage(
        enabled = enabled,
        image = link,
        description = description,
        width = 32,
        height = 32,
        color = color
    ).Content(modifier = modifier)

    /**
     * Lays out the indicator for the objective's permanent or temporary waypoint if it exists.
     */
    @Composable
    protected fun ObjectiveWaypoint.Waypoint(modifier: Modifier) = AsyncImage(
        enabled = enabled,
        image = link,
        description = description,
        width = 32,
        height = 32,
        color = color
    ).Content(modifier = modifier)

    /**
     * Lays out the image of the objective.
     */
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    protected fun ObjectiveIcon.Image(modifier: Modifier, width: Int, height: Int) {
        val mapRouter = LocalMapRouter.current
        AsyncImage(
            width = width,
            height = height,
            image = link,
            description = description,
            color = color
        ).Content(
            modifier = modifier.combinedClickable(
                onLongClick = {
                    // Swap pages to display all of the information instead of the limited information that normally comes with the pop-up.
                    val config = MapConfig.ObjectiveConfig(objective.id.value)
                    mapRouter.bringToFront(config)
                },
                onClick = {
                    // Set the selected objective for displaying the pop-up.
                    model.selected.value = objective
                }
            ),
        )
    }

    /**
     * Lays out the timer for the amount of time an objective is immune from capture.
     */
    @Composable
    protected fun ObjectiveImmunity.ImmunityTimer(modifier: Modifier) {
        // If the time has finished or the current time is incorrectly set and thus causing an inflated remaining time, do not display it.
        // For the latter case, while the timers shown will be incorrect they will at the very least not be inflated.
        val remaining = remaining.collectAsState(initial = Duration.ZERO).value
        if (!remaining.isPositive() || duration == null || remaining > duration) return

        Text(
            text = remaining.minuteFormat(),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.White,
            modifier = modifier.wrapContentSize()
        )
    }
}