package com.bselzer.gw2.manager.ui.activity

import android.app.Application
import com.bselzer.gw2.manager.companion.AppCompanion

/**
 * The application used for initialization.
 */
class AppInitializer : Application() {
    override fun onCreate() {
        super.onCreate()

        AppCompanion.initialize(this)
    }
}