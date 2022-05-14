package com.bselzer.gw2.manager.common.ui.context

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.Router
import com.arkivanov.decompose.router.router
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.bselzer.gw2.manager.common.dependency.Dependencies
import com.bselzer.gw2.manager.common.ui.base.Configuration
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import kotlin.reflect.KClass

class Gw2ComponentContext(
    dependencies: Dependencies,
    component: ComponentContext = DefaultComponentContext(LifecycleRegistry())
) : AppComponentContext, Dependencies by dependencies, ComponentContext by component {
    override fun <Config : Configuration, Model : ViewModel> createRouter(
        initialStack: () -> List<Config>,
        configurationClass: KClass<out Config>,
        key: String,
        handleBackButton: Boolean,
        childFactory: (configuration: Config, AppComponentContext) -> Model
    ): Router<Config, Model> = router(
        initialStack = initialStack,
        configurationClass = configurationClass,
        key = key,
        handleBackButton = handleBackButton,
    ) { configuration, componentContext ->
        childFactory(
            configuration,
            Gw2ComponentContext(this, componentContext)
        )
    }
}