package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.content

import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import com.bselzer.gw2.manager.common.ui.layout.common.ConstrainedModifierScope

class UpgradeTierModifiers(override val scope: ConstraintLayoutScope) : ConstrainedModifierScope {
    private val iconRef = createRef()
    private val descriptorRef = createRef()
    private val expansionRef = createRef()

    val icon = iconRef.modifier {
        top.linkTo(parent.top)
        start.linkTo(parent.start)
        bottom.linkTo(parent.bottom)
    }

    val descriptor = descriptorRef.modifier {
        top.linkTo(parent.top)
        start.linkTo(iconRef.end, margin = 5.dp)
        end.linkTo(expansionRef.start, margin = 5.dp)
        bottom.linkTo(parent.bottom)
        width = Dimension.fillToConstraints
    }

    val expansion = expansionRef.modifier {
        top.linkTo(parent.top)
        end.linkTo(parent.end)
        bottom.linkTo(parent.bottom)
    }
}