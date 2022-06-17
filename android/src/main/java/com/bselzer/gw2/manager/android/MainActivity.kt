package com.bselzer.gw2.manager.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.backpressed.BackPressedHandler
import com.arkivanov.essenty.lifecycle.essentyLifecycle
import com.arkivanov.essenty.statekeeper.stateKeeper
import com.bselzer.gw2.manager.common.dependency.AndroidApp
import com.bselzer.gw2.manager.common.dependency.AppDependencies
import com.bselzer.gw2.manager.common.ui.base.Gw2ComponentContext
import com.bselzer.gw2.manager.common.ui.layout.host.content.HostComposition
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.HostViewModel
import com.bselzer.ktx.logging.Logger

class MainActivity : AppCompatActivity() {
    private var dependencies: AppDependencies? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensure the previous database lock is cleared before recreating in case onDestroy() does not complete.
        release()

        // Must keep the datastore in the application so that there is only one active at a time.
        val datastore = with(application) {
            val initializer = this as AppInitializer
            initializer.datastore
        }

        // Initialize dependencies before composing since they won't change.
        val app = AndroidApp(this, datastore).apply {
            this@MainActivity.dependencies = dependencies
        }

        // Initialize the component context before composing to avoid potentially creating on another thread.
        // https://arkivanov.github.io/Decompose/component/overview/#root-componentcontext-in-jetpackjetbrains-compose
        val host = HostViewModel(app.dependencies.createContext())

        setContent {
            app.Content {
                HostComposition(host).Content()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }

    private fun release() = try {
        // Close the database so that it isn't locked anymore.
        // Otherwise, an exception will be thrown upon recreation of the activity.
        dependencies?.database?.close()
    } catch (ex: Exception) {
        Logger.e(ex) { "Failed to close the LevelDB database." }
    } finally {
        dependencies = null
    }

    private fun AppDependencies.createContext() = Gw2ComponentContext(
        dependencies = this,

        // Not using androidx.lifecycle.ViewModel so there is no instance keeper passed.
        // https://github.com/arkivanov/Essenty#instancekeeper
        component = DefaultComponentContext(
            // Establish the connection between the activity's lifecycle and essenty's lifecycle.
            // https://github.com/arkivanov/Essenty#lifecyle
            // https://arkivanov.github.io/Decompose/component/lifecycle/
            lifecycle = essentyLifecycle(),

            // Establish the connection between the activity's SavedStateRegistry and essenty's state keeper.
            // https://github.com/arkivanov/Essenty#statekeeper
            // https://arkivanov.github.io/Decompose/component/state-preservation/
            stateKeeper = stateKeeper(),

            // Establish the connection between the activity's OnBackPressedDispatcher and essenty's handler.
            // https://github.com/arkivanov/Essenty#backpresseddispatcher
            // https://arkivanov.github.io/Decompose/component/back-button/
            backPressedHandler = BackPressedHandler(onBackPressedDispatcher)
        )
    )
}