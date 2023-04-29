package com.bselzer.gw2.manager.common.ui.base

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.bselzer.gw2.manager.common.dependency.AppDependencies
import kotlin.reflect.KClass

interface AppComponentContext : AppDependencies, ComponentContext {
    fun <Config : Configuration, Model : ViewModel> createRouter(
        source: StackNavigation<Config> = StackNavigation(),
        configurationClass: KClass<Config>,
        initialStack: () -> List<Config>,
        key: String,
        handleBackButton: Boolean = false,
        childFactory: (configuration: Config, AppComponentContext) -> Model
    ): Router<Config, Model>
}