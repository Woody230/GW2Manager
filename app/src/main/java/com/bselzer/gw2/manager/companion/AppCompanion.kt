package com.bselzer.gw2.manager.companion

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import coil.ImageLoader
import com.bselzer.gw2.manager.BuildConfig
import com.bselzer.gw2.manager.companion.PreferenceCompanion.DATASTORE
import com.bselzer.gw2.manager.companion.PreferenceCompanion.TOKEN
import com.bselzer.library.gw2.v2.client.client.Gw2Client
import com.bselzer.library.kotlin.extension.preference.nullLatest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
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
            // Initialize the client with the token if it exists.
            val token = DATASTORE.nullLatest(TOKEN) ?: return@launch
            GW2 = GW2.config { copy(token = token) }
            Timber.d("Set client token to $token")
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