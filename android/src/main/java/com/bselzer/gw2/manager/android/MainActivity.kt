package com.bselzer.gw2.manager.android

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bselzer.gw2.manager.common.AndroidApp
import com.bselzer.gw2.manager.common.ui.base.Gw2ComponentContext
import com.bselzer.gw2.manager.common.ui.layout.host.content.HostComposition
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.HostViewModel

class MainActivity : AppCompatActivity() {
    /**
     * The default preferences datastore.
     */
    private val Context.datastore: DataStore<Preferences> by preferencesDataStore("default")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = AndroidApp(this, datastore).apply { initialize() }
        val context = Gw2ComponentContext(app)
        val host = HostViewModel(context)

        setContent {
            app.Content {
                HostComposition().Content(model = host)
            }
        }
    }
}