package com.bselzer.gw2.manager.common.ui.base

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.value.Value

data class Router<Config : Configuration, Model : ViewModel>(
    val childStack: Value<ChildStack<Config, Model>>,
    private val navigation: StackNavigation<Config>
) : StackNavigation<Config> by navigation {
    val activeChild: Child.Created<Config, Model>
        get() = childStack.active
}