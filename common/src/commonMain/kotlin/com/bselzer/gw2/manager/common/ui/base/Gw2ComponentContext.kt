package com.bselzer.gw2.manager.common.ui.base

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.bselzer.gw2.manager.common.dependency.AppDependencies
import com.bselzer.ktx.logging.Logger
import kotlinx.serialization.KSerializer

class Gw2ComponentContext(
    dependencies: AppDependencies,
    component: ComponentContext = DefaultComponentContext(LifecycleRegistry())
) : AppComponentContext, AppDependencies by dependencies, ComponentContext by component {
    override fun <Config : Configuration, Model : ViewModel> createRouter(
        source: StackNavigation<Config>,
        initialStack: () -> List<Config>,
        serializer: KSerializer<Config>,
        key: String,
        handleBackButton: Boolean,
        childFactory: (configuration: Config, AppComponentContext) -> Model
    ): Router<Config, Model> {
        val childStack = childStack(
            source = source,
            initialStack = initialStack,
            serializer = serializer,
            key = key,
            handleBackButton = handleBackButton,
        ) { configuration, componentContext ->
            Logger.d { "Router $key | Configuration ${configuration::class.simpleName}" }
            childFactory(
                configuration,
                Gw2ComponentContext(this, componentContext)
            )
        }

        return Router(childStack, source)
    }
}