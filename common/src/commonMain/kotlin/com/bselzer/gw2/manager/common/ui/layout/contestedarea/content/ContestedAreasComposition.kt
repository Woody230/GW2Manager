package com.bselzer.gw2.manager.common.ui.layout.contestedarea.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.bselzer.gw2.manager.common.ui.base.ShouldLayoutHorizontally
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.model.ContestedObjective
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.model.ContestedPointsPerTick
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.viewmodel.ContestedAreasViewModel
import com.bselzer.gw2.manager.common.ui.layout.image.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.image.Content
import com.bselzer.ktx.compose.resource.strings.localized

interface ContestedAreasComposition<Model : ContestedAreasViewModel> {
    @Composable
    fun Model.ContestedAreasContent(
        modifier: Modifier = Modifier
    ) = ConstraintLayout(
        modifier = modifier,
    ) {
        val refs = contestedObjectives.map { objectives -> objectives.map { createRef() } }
        PointsPerTick(scope = this, refs = refs)
        Objectives(scope = this, refs = refs)
    }

    @Composable
    private fun ContestedAreasViewModel.PointsPerTick(
        scope: ConstraintLayoutScope,
        refs: List<List<ConstrainedLayoutReference>>
    ) = with(scope) {
        pointsPerTick.forEachIndexed { row, pointsPerTick ->
            val startMargin = if (ShouldLayoutHorizontally) 20.dp else 10.dp
            pointsPerTick.Content(modifier = Modifier.constrainAs(createRef()) {
                // Add points per tick after the last column.
                val objectiveRef = refs[contestedObjectives.lastIndex][row]
                start.linkTo(objectiveRef.end, margin = startMargin)

                // Center within the row.
                top.linkTo(objectiveRef.top)
                bottom.linkTo(objectiveRef.bottom)
            })
        }
    }

    @Composable
    private fun ContestedAreasViewModel.Objectives(
        scope: ConstraintLayoutScope,
        refs: List<List<ConstrainedLayoutReference>>
    ) = with(scope) {
        contestedObjectives.forEachIndexed { column, objectives ->
            objectives.forEachIndexed { row, objective ->
                val ref = refs[column][row]
                objective.Content(modifier = Modifier.constrainAs(ref) {
                    linkObjectiveTop(column, row, refs)
                    linkObjectiveStart(column, row, refs)
                })
            }
        }
    }

    private fun ConstrainScope.linkObjectiveTop(
        column: Int,
        row: Int,
        refs: List<List<ConstrainedLayoutReference>>
    ) {
        val topRef = if (row == 0) parent.top else refs[column][row - 1].bottom
        top.linkTo(topRef)
    }

    private fun ConstrainScope.linkObjectiveStart(
        column: Int,
        row: Int,
        refs: List<List<ConstrainedLayoutReference>>
    ) {
        val startRef = when {
            // First cell
            row == 0 && column == 0 -> parent.start

            // Top row cell => end of previous cell in row
            row == 0 -> refs[column - 1][row].end

            // Start of previous cell in column
            else -> refs[column][row - 1].start
        }

        val startMargin = when {
            // Add margin only to intermediary top row cells
            row == 0 && column != 0 -> 5.dp
            else -> 0.dp
        }

        start.linkTo(startRef, startMargin)
    }

    @Composable
    private fun ContestedPointsPerTick.Content(modifier: Modifier) = Text(
        modifier = modifier,
        text = ppt.localized(),
        color = color,
        fontSize = 32.sp
    )

    @Composable
    private fun ContestedObjective.Content(
        modifier: Modifier
    ) = when (ShouldLayoutHorizontally) {
        true -> HorizontalContent(modifier)
        false -> VerticalContent(modifier)
    }

    @Composable
    private fun ContestedObjective.HorizontalContent(
        modifier: Modifier
    ) = Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon()
        Count()
    }

    @Composable
    private fun ContestedObjective.VerticalContent(
        modifier: Modifier
    ) = Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon()
        Count()
    }

    @Composable
    private fun ContestedObjective.Icon() = AsyncImage(
        image = link,
        color = color,
        description = description,
        size = DpSize(50.dp, 50.dp),
    ).Content()

    @Composable
    private fun ContestedObjective.Count() = Text(text = count.localized())
}