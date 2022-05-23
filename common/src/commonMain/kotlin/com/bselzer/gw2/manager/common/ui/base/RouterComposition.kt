package com.bselzer.gw2.manager.common.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.ChildContent
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.animation.child.ChildAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.animation.child.crossfade
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.router.Router
import com.bselzer.ktx.logging.Logger

@OptIn(ExperimentalDecomposeApi::class)
abstract class RouterComposition<Config : Configuration, Model : ViewModel>(
    private val router: @Composable () -> Router<Config, Model>
) {
    /**
     * Lays out the content for the currently active child of the router.
     */
    @Composable
    fun Content(modifier: Modifier = Modifier) = ChildContent(
        modifier = modifier
    ) { child ->
        val model = child.instance
        Logger.d { "${this@RouterComposition::class.simpleName}: ${model::class.simpleName}" }
        model.Content()
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
    protected abstract fun Model.Content()

    /**
     * Lays out the content for the currently active child's model and configuration.
     */
    @Composable
    protected fun ChildContent(
        modifier: Modifier = Modifier,
        content: ChildContent<Config, Model>
    ) = router().run {
        Children(
            routerState = state,
            modifier = modifier,
            animation = animation(),
            content = content
        )
    }

    /**
     * @return the active child of the remembered router state
     */
    @Composable
    protected fun rememberActiveChild() = router().state.subscribeAsState().run {
        this.value.activeChild
    }
}