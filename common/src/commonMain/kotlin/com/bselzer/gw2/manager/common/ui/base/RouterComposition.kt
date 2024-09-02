package com.bselzer.gw2.manager.common.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.bselzer.ktx.logging.Logger

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
        Logger.d { "Router ${this@RouterComposition::class.simpleName} | Model ${model::class.simpleName}" }
        model.Content()
    }

    /**
     * Creates an animation to use when swapping between children.
     */
    @Composable
    protected open fun animation(): StackAnimation<Config, Model> = stackAnimation()

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
        content: @Composable (Child.Created<Config, Model>) -> Unit
    ) = router().run {
        Children(
            stack = childStack,
            modifier = modifier,
            animation = animation(),
            content = content
        )
    }

    /**
     * @return the active child of the remembered router state
     */
    @Composable
    protected fun rememberActiveChild() = router().childStack.subscribeAsState().run {
        this.value.active
    }
}