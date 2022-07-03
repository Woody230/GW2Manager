package com.bselzer.gw2.manager.common.ui.layout.contestedarea.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.base.ShouldLayoutHorizontally
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.viewmodel.ContestedAreasViewModel

class ConstrainedComposition(
    model: ContestedAreasViewModel,
    private val constraintScope: ConstraintLayoutScope
) : ModelComposition<ContestedAreasViewModel>(model) {
    private val refs = with(constraintScope) {
        model.contestedObjectives.map { objectives -> objectives.map { createRef() } }
    }

    @Composable
    override fun ContestedAreasViewModel.Content(
        modifier: Modifier
    ) = with(constraintScope) {
        PointsPerTick()
        Objectives()
    }

    @Composable
    private fun ConstraintLayoutScope.PointsPerTick() = model.pointsPerTick.forEachIndexed { row, pointsPerTick ->
        PointsPerTickComposition(pointsPerTick).Content(modifier = PointsPerTickModifier(row))
    }

    @Composable
    private fun ConstraintLayoutScope.PointsPerTickModifier(row: Int): Modifier {
        val startMargin = if (ShouldLayoutHorizontally) 20.dp else 10.dp
        return Modifier.constrainAs(createRef()) {
            linkPointsPerTick(row, startMargin)
        }
    }

    private fun ConstrainScope.linkPointsPerTick(row: Int, startMargin: Dp) {
        // Add after the last column.
        val objectiveRef = refs[model.contestedObjectives.lastIndex][row]
        start.linkTo(objectiveRef.end, margin = startMargin)

        // Center within the row.
        top.linkTo(objectiveRef.top)
        bottom.linkTo(objectiveRef.bottom)
    }

    @Composable
    private fun ConstraintLayoutScope.Objectives() = model.contestedObjectives.forEachIndexed { column, objectives ->
        objectives.forEachIndexed { row, objective ->
            ObjectiveComposition(objective).Content(modifier = ObjectiveModifier(column, row))
        }
    }

    @Composable
    private fun ConstraintLayoutScope.ObjectiveModifier(
        column: Int,
        row: Int,
    ): Modifier {
        val ref = refs[column][row]
        return Modifier.constrainAs(ref) {
            linkObjectiveHorizontally(column, row)
            linkObjectiveVertically(column, row)
        }
    }

    private fun ConstrainScope.linkObjectiveVertically(
        column: Int,
        row: Int
    ) {
        val topRef = if (row == 0) parent.top else refs[column][row - 1].bottom
        top.linkTo(topRef)
    }

    private fun ConstrainScope.linkObjectiveHorizontally(
        column: Int,
        row: Int
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
}