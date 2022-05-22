package com.bselzer.gw2.manager.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.bselzer.gw2.manager.common.dependency.JvmApp
import com.bselzer.gw2.manager.common.ui.base.Gw2ComponentContext
import com.bselzer.gw2.manager.common.ui.layout.host.content.HostComposition
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.HostViewModel

fun main() {
    // Initialize dependencies before composing since they won't change.
    val app = JvmApp().apply {
        initialize()
    }

    // Initialize the component context before composing to avoid potentially creating on another thread.
    // https://arkivanov.github.io/Decompose/component/overview/#root-componentcontext-in-jetpackjetbrains-compose
    val context = Gw2ComponentContext(
        dependencies = app,
        component = DefaultComponentContext(LifecycleRegistry())
    )

    val host = HostViewModel(context)
    application {
        Window(onCloseRequest = ::exitApplication) {
            // TODO tailor to desktop
            //  components can be split into multiple columns due to bigger screen size
            //  scaffold modal drawer should always be visible by default
            //  constrained dialog instead of normal alert dialog will create a window with a title (of "Untitled")
            app.Content {
                HostComposition().Content(model = host)
            }
        }
    }
}