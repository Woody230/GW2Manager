package com.bselzer.gw2.manager.common.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.animation.child.ChildAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.animation.child.crossfade
import com.arkivanov.decompose.router.Router
import com.bselzer.ktx.logging.Logger

@OptIn(ExperimentalDecomposeApi::class)
abstract class RouterComposition<Config : Configuration, Model : ViewModel>(
    protected val router: @Composable () -> Router<Config, Model>
) {
    /**
     * Lays out the content for the currently active child of the router.
     */
    @Composable
    fun Content(modifier: Modifier = Modifier) = router().run {
        Children(
            routerState = state,
            modifier = modifier,
            animation = animation(),
            content = { child ->
                val model = child.instance
                Logger.d { "${this@RouterComposition::class.simpleName}: ${model::class.simpleName}" }
                model.Content()
            }
        )
    }

    /**
     * Creates an animation to use when swapping between children.
     */
    @Composable
    protected open fun animation(): ChildAnimation<Config, Model> = crossfade()

    /**
     * Lays out the content for the currently active child's model and configuration.
     */
    @Composable
    abstract fun Model.Content()
}