package com.bselzer.gw2.manager.companion

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import coil.ImageLoader
import com.bselzer.gw2.manager.BuildConfig
import com.bselzer.gw2.manager.companion.preference.PreferenceCompanion.DATASTORE
import com.bselzer.gw2.manager.companion.preference.PreferenceCompanion.TOKEN
import com.bselzer.gw2.manager.companion.preference.WvwPreferenceCompanion
import com.bselzer.gw2.manager.configuration.Configuration
import com.bselzer.library.gw2.v2.client.client.Gw2Client
import com.bselzer.library.gw2.v2.tile.client.TileClient
import com.bselzer.library.kotlin.extension.preference.initialize
import com.bselzer.library.kotlin.extension.preference.nullLatest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.OkHttpClient
import timber.log.Timber

object AppCompanion
{
    /**
     * Initialize objects that rely on the application being created.
     */
    fun initialize(application: Application) {
        APPLICATION = application

        CONFIG = application.getConfiguration()
        IMAGE_LOADER = application.getImageLoader()
        DATASTORE = application.DATASTORE.apply {
            setupDatastore()
        }
    }

    /**
     * @return the configuration
     */
    private fun Application.getConfiguration(): Configuration {
        // TODO attempt to get config from online location and default to bundled config if that fails
        val config = assets.open("Configuration.xml").bufferedReader(Charsets.UTF_8).use { reader -> reader.readText() }
        return XML.decodeFromString(Configuration.serializer(), config)
    }

    /**
     * @return the Coil image loader
     */
    // TODO custom disk cache? https://coil-kt.github.io/coil/image_loaders/#caching
    private fun Application.getImageLoader(): ImageLoader = ImageLoader.Builder(this).okHttpClient(HTTP_CLIENT).build()

    /**
     * Sets up the datastore and information relying on the datastore.
     */
    private fun DataStore<Preferences>.setupDatastore() {
        CoroutineScope(Dispatchers.IO).launch {
            // Default preferences.
            initialize(WvwPreferenceCompanion.REFRESH_INTERVAL, 5)

            // Initialize the client with the token if it exists.
            val token = nullLatest(TOKEN) ?: return@launch
            GW2 = GW2.config { copy(token = token) }
            Timber.d("Set client token to $token")
        }
    }

    /**
     * The application.
     */
    lateinit var APPLICATION: Application

    /**
     * The configuration.
     */
    lateinit var CONFIG: Configuration

    /**
     * The common preferences.
     */
    lateinit var DATASTORE: DataStore<Preferences>

    /**
     * The image loader.
     */
    lateinit var IMAGE_LOADER: ImageLoader

    /**
     * The HTTP client to share across all instances.
     */
    val HTTP_CLIENT: OkHttpClient

    /**
     * The GW2 API wrapper.
     */
    var GW2: Gw2Client

    /**
     * The Gw2 tile client.
     */
    val TILE: TileClient

    init {
        // Only enable logging for debug mode.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        HTTP_CLIENT = OkHttpClient()
        val httpClient = HttpClient(OkHttp) {
            engine {
                preconfigured = HTTP_CLIENT
            }

            HttpResponseValidator {
                handleResponseException { ex -> Timber.e(ex) }
            }
        }

        GW2 = Gw2Client(httpClient = httpClient)
        TILE = TileClient(httpClient = httpClient)
    }
}