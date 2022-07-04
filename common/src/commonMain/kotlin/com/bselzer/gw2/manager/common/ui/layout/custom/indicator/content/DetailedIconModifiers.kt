package com.bselzer.gw2.manager.common.ui.layout.custom.indicator.content

import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.bselzer.gw2.manager.common.ui.layout.common.ConstrainedModifierScope

class DetailedIconModifiers(override val scope: ConstraintLayoutScope) : ConstrainedModifierScope {
    private val iconRef = createRef()
    private val immunityRef = createRef()
    private val progressionRef = createRef()
    private val claimRef = createRef()
    private val waypointRef = createRef()

    val icon = iconRef.modifier {
        top.linkTo(parent.top)
        start.linkTo(parent.start)
    }

    val progression = progressionRef.modifier {
        // Display the indicator in the top center of the objective icon.
        top.linkTo(iconRef.top)
        start.linkTo(iconRef.start)
        end.linkTo(iconRef.end)
    }

    val claim = claimRef.modifier {
        // Display the indicator in the bottom right of the objective icon.
        bottom.linkTo(iconRef.bottom)
        end.linkTo(iconRef.end)
    }

    val waypoint = waypointRef.modifier {
        // Display the indicator in the bottom left of the objective icon.
        bottom.linkTo(iconRef.bottom)
        start.linkTo(iconRef.start)
    }

    val immunity = immunityRef.modifier {
        // Display the timer underneath the objective icon.
        top.linkTo(iconRef.bottom)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
    }
}