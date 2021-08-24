package com.bselzer.gw2.manager.companion

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import coil.ImageLoader
import com.bselzer.gw2.manager.BuildConfig
import com.bselzer.gw2.manager.companion.PreferenceCompanion.DEFAULT_PREFERENCES
import com.bselzer.library.gw2.v2.client.client.Gw2Client
import timber.log.Timber

object AppCompanion
{
    init
    {
        // Only enable logging for debug mode.
        if (BuildConfig.DEBUG)
        {
            Timber.plant(Timber.DebugTree())
        }
    }

    /**
     * Initialize objects that rely on the application being created.
     */
    fun initialize(application: Application)
    {
        APPLICATION = application

        DEFAULT_PREFERENCES = application.DEFAULT_PREFERENCES

        // TODO custom disk cache? https://coil-kt.github.io/coil/image_loaders/#caching
        IMAGE_LOADER = ImageLoader.Builder(application).build()

        GW2 = Gw2Client()
    }

    /**
     * The application.
     */
    lateinit var APPLICATION: Application

    /**
     * The common preferences.
     */
    lateinit var DEFAULT_PREFERENCES: DataStore<Preferences>

    /**
     * The image loader.
     */
    lateinit var IMAGE_LOADER: ImageLoader

    /**
     * The GW2 API wrapper.
     */
    lateinit var GW2: Gw2Client
}