package com.bselzer.gw2.manager.common.ui.base

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.Router
import com.bselzer.gw2.manager.common.dependency.Dependencies
import kotlin.reflect.KClass

interface AppComponentContext : Dependencies, ComponentContext {
    fun <Config : Configuration, Model : ViewModel> createRouter(
        initialStack: () -> List<Config>,
        configurationClass: KClass<out Config>,
        key: String = "DefaultRouter",
        handleBackButton: Boolean = false,
        childFactory: (configuration: Config, AppComponentContext) -> Model
    ): Router<Config, Model>
}