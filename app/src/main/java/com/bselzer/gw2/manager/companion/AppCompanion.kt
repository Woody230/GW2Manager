package com.bselzer.gw2.manager.companion

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import coil.ImageLoader
import com.bselzer.gw2.manager.BuildConfig
import com.bselzer.gw2.manager.companion.PreferenceCompanion.API_KEY
import com.bselzer.gw2.manager.companion.PreferenceCompanion.DATASTORE
import com.bselzer.library.gw2.v2.client.client.Gw2Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
    fun initialize(application: Application) {
        APPLICATION = application

        DATASTORE = application.DATASTORE

        // TODO custom disk cache? https://coil-kt.github.io/coil/image_loaders/#caching
        IMAGE_LOADER = ImageLoader.Builder(application).build()

        CoroutineScope(Dispatchers.IO).launch {
            // Keep the token in the client updated as it is changed using a StateFlow.
            DATASTORE.data.stateIn(this).map { pref -> pref[API_KEY] }.filter { key -> !key.isNullOrBlank() }.collect { key ->
                GW2 = GW2.config { copy(token = key) }
                Timber.d("Set client token to $key")
            }
        }
    }

    /**
     * The application.
     */
    lateinit var APPLICATION: Application

    /**
     * The common preferences.
     */
    lateinit var DATASTORE: DataStore<Preferences>

    /**
     * The image loader.
     */
    lateinit var IMAGE_LOADER: ImageLoader

    /**
     * The GW2 API wrapper.
     */
    var GW2: Gw2Client = Gw2Client()
}