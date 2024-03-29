package com.bselzer.gw2.manager.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.essenty.backhandler.BackHandler
import com.arkivanov.essenty.lifecycle.essentyLifecycle
import com.arkivanov.essenty.statekeeper.stateKeeper
import com.bselzer.gw2.manager.common.dependency.AndroidApp
import com.bselzer.gw2.manager.common.dependency.AppDependencies
import com.bselzer.gw2.manager.common.ui.base.Gw2ComponentContext
import com.bselzer.gw2.manager.common.ui.layout.host.content.HostComposition
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.HostViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.configuration.SplashConfig
import com.bselzer.ktx.logging.Logger

class MainActivity : AppCompatActivity() {
    private var dependencies: AppDependencies? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d { "MainActivity | Lifecycle | Create" }

        // Ensure the previous database lock is cleared before recreating in case onDestroy() does not complete.
        release()

        // Must keep the datastore in the application so that there is only one active at a time.
        val datastore = with(application) {
            val initializer = this as AppInitializer
            initializer.datastore
        }

        // Initialize dependencies before composing since they won't change.
        val app = AndroidApp(context = this, scope = lifecycleScope, datastore = datastore)
        dependencies = app.dependencies

        // Initialize the component context before composing to avoid potentially creating on another thread.
        // https://arkivanov.github.io/Decompose/component/overview/#root-componentcontext-in-jetpackjetbrains-compose
        val host = HostViewModel(app.dependencies.createContext())

        // Normally the router state is saved and no splash screen will be displayed upon recreation.
        // Therefore we must ensure that initialization reoccurs by explicitly setting the config.
        host.splashRouter.bringToFront(SplashConfig.InitializationConfig)

        setContent {
            app.Content {
                HostComposition(host).Content()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Logger.d { "MainActivity | Lifecycle | Start" }
    }

    override fun onResume() {
        super.onResume()
        Logger.d { "MainActivity | Lifecycle | Resume" }
    }

    override fun onPause() {
        super.onPause()
        Logger.d { "MainActivity | Lifecycle | Pause" }
    }

    override fun onRestart() {
        super.onRestart()
        Logger.d { "MainActivity | Lifecycle | Restart" }
    }

    override fun onStop() {
        super.onStop()
        Logger.d { "MainActivity | Lifecycle | Stop" }
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d { "MainActivity | Lifecycle | Destroy" }
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
            backHandler = BackHandler(onBackPressedDispatcher)
        )
    )
}