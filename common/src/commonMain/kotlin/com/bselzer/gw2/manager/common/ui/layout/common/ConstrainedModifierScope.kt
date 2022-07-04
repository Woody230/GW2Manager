package com.bselzer.gw2.manager.common.ui.layout.common

import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutScope

interface ConstrainedModifierScope {
    val scope: ConstraintLayoutScope

    fun createRef(): ConstrainedLayoutReference = with(scope) { createRef() }

    fun ConstrainedLayoutReference.modifier(
        block: ConstrainScope.() -> Unit
    ) = with(scope) {
        Modifier.constrainAs(this@modifier, block)
    }
}