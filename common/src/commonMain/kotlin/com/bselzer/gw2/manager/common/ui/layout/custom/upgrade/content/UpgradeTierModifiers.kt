package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.content

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension

class UpgradeTierModifiers(scope: ConstraintLayoutScope) {
    private val iconRef = with(scope) { createRef() }
    private val descriptorRef = with(scope) { createRef() }
    private val expansionRef = with(scope) { createRef() }

    val icon = with(scope) {
        Modifier.constrainAs(iconRef) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            bottom.linkTo(parent.bottom)
        }
    }

    val descriptor = with(scope) {
        Modifier.constrainAs(descriptorRef) {
            top.linkTo(parent.top)
            start.linkTo(iconRef.end, margin = 5.dp)
            end.linkTo(expansionRef.start, margin = 5.dp)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
        }
    }

    val expansion = with(scope) {
        Modifier.constrainAs(expansionRef) {
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }
    }
}