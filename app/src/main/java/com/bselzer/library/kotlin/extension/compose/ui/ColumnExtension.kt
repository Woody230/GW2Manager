package com.bselzer.library.kotlin.extension.compose.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

/**
 * Displays a row with values aligned from the center with [spacing].
 */
@Composable
fun ShowCenteredRow(startValue: String, endValue: String, spacing: Dp = 5.dp) = ConstraintLayout(
    modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
) {
    val (left, spacer, right) = createRefs()
    Text(text = startValue, textAlign = TextAlign.Right, modifier = Modifier.constrainAs(left) {
        top.linkTo(parent.top)
        bottom.linkTo(parent.bottom)
        start.linkTo(parent.start)
        end.linkTo(spacer.start)
        width = Dimension.fillToConstraints
    })
    Spacer(modifier = Modifier
        .width(spacing)
        .constrainAs(spacer) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(left.end)
            end.linkTo(right.start)
        })
    Text(text = endValue, textAlign = TextAlign.Left, modifier = Modifier.constrainAs(right) {
        top.linkTo(parent.top)
        bottom.linkTo(parent.bottom)
        start.linkTo(spacer.end)
        end.linkTo(parent.end)
        width = Dimension.fillToConstraints
    })
}