package com.bselzer.gw2.manager.ui.activity

import android.app.Application
import com.bselzer.gw2.manager.BuildConfig
import timber.log.Timber

/**
 * The application used for initialization.
 */
class AppInitializer : Application()
{
    override fun onCreate()
    {
        super.onCreate()

        // Only enable logging for debug mode.
        if (BuildConfig.DEBUG)
        {
            Timber.plant(Timber.DebugTree())
        }
    }
}