package com.bselzer.gw2.manager.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.bselzer.gw2.manager.common.AndroidApp
import com.bselzer.gw2.manager.common.dependency.Dependencies
import com.bselzer.gw2.manager.common.ui.base.Gw2ComponentContext
import com.bselzer.gw2.manager.common.ui.layout.host.content.HostComposition
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.HostViewModel
import com.bselzer.ktx.logging.Logger

class MainActivity : AppCompatActivity() {
    private var dependencies: Dependencies? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Must keep the datastore in the application so that there is only one active at a time.
        val datastore = with(application) {
            val initializer = this as AppInitializer
            initializer.datastore
        }

        // Initialize dependencies before composing since they won't change.
        val app = AndroidApp(this, datastore).apply {
            initialize()
            dependencies = this
        }

        // Initialize the component context before composing to avoid potentially creating on another thread.
        // https://arkivanov.github.io/Decompose/component/overview/#root-componentcontext-in-jetpackjetbrains-compose
        val context = Gw2ComponentContext(app)
        val host = HostViewModel(context)

        setContent {
            app.Content {
                HostComposition().Content(model = host)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            // Close the database so that it isn't locked anymore.
            // Otherwise, an exception will be thrown upon recreation of the activity.
            dependencies?.caches?.database?.close()
        } catch (ex: Exception) {
            Logger.e(ex) { "Failed to close the LevelDB database." }
        }
    }
}