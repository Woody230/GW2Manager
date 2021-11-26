package com.bselzer.gw2.manager.ui.activity.common

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import coil.ImageLoader
import coil.util.CoilUtils
import com.bselzer.gw2.manager.BuildConfig
import com.bselzer.gw2.manager.configuration.Configuration
import com.bselzer.gw2.manager.ui.activity.setting.preference.CommonPreference
import com.bselzer.gw2.manager.ui.activity.setting.preference.WvwPreference
import com.bselzer.library.gw2.v2.cache.metadata.IdentifiableMetadataExtractor
import com.bselzer.library.gw2.v2.cache.provider.Gw2CacheProvider
import com.bselzer.library.gw2.v2.cache.type.gw2
import com.bselzer.library.gw2.v2.client.client.ExceptionRecoveryMode
import com.bselzer.library.gw2.v2.client.client.Gw2Client
import com.bselzer.library.gw2.v2.client.client.Gw2ClientConfiguration
import com.bselzer.library.gw2.v2.emblem.client.EmblemClient
import com.bselzer.library.gw2.v2.model.serialization.Modules
import com.bselzer.library.gw2.v2.tile.cache.instance.TileCache
import com.bselzer.library.gw2.v2.tile.cache.metadata.TileMetadataExtractor
import com.bselzer.library.gw2.v2.tile.client.TileClient
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.http.*
import kotlinx.serialization.modules.SerializersModule
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.UnknownChildHandler
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.*
import okhttp3.internal.http.RealResponseBody
import okio.Buffer
import org.kodein.db.DB
import org.kodein.db.TypeTable
import org.kodein.db.impl.inDir
import org.kodein.db.orm.kotlinx.KotlinxSerializer
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import timber.log.Timber

/**
 * The application used for initialization.
 */
class AppInitializer : Application(), DIAware {
    init {
        Timber.uprootAll()

        // Only enable logging for debug mode.
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    // TODO try wrappers around cache/clients

    private companion object {
        /**
         * The default preferences datastore.
         */
        val Context.DATASTORE: DataStore<Preferences> by preferencesDataStore("default")

        /**
         * The name of the GW2 user agent.
         */
        const val GW2_USER_AGENT: String = "gw2"

        /**
         * The name of the GW2 emblem user agent.
         */
        const val EMBLEM_USER_AGENT: String = "gw2-emblem"
    }

    override val di: DI by DI.lazy {
        bindHttpClient()
        bindDatabase()
        bindGw2()
        bindConfiguration()
        bindImageLoader()
        bindSingleton(tag = "InitialDataPopulation") { mutableStateOf(false) }
        bindPreferences()
    }

    /**
     * Binds the preference datastore wrappers.
     */
    private fun DI.MainBuilder.bindPreferences() {
        bindSingleton { CommonPreference(DATASTORE) }
        bindSingleton { WvwPreference(DATASTORE) }
    }

    /**
     * Binds the HTTP client for Ktor and Coil.
     */
    private fun DI.MainBuilder.bindHttpClient() {
        bindSingleton {
            OkHttpClient.Builder()
                .cache(CoilUtils.createDefaultCache(this@AppInitializer))
                .addInterceptor { chain ->
                    var request: Request? = null
                    try {
                        request = chain.request()
                        Timber.d("Intercepted ${request.url}")

                        val originalResponse = chain.proceed(request)
                        val isCacheableRequest = request.headers[HttpHeaders.UserAgent] != GW2_USER_AGENT

                        // Do not cache anything coming from the GW2/Tile clients. That should only be left to Kodein-DB.
                        if (isCacheableRequest) originalResponse else originalResponse.newBuilder().header("Cache-Control", "no-store").build()
                    } catch (exception: Exception) {
                        Timber.e(exception)

                        // Fake the response as needed.
                        request = request ?: Request.Builder().url("").build()
                        Response.Builder().code(0).protocol(Protocol.HTTP_2).body(RealResponseBody("text/plain", 0, Buffer())).request(request).message(exception.message ?: "").build()
                    }
                }
                .build()
        }
        bindSingleton {
            HttpClient(OkHttp) {
                engine {
                    // Set the bound OkHttpClient.
                    preconfigured = instance()
                }

                install(UserAgent) {
                    agent = GW2_USER_AGENT
                }

                HttpResponseValidator {
                    handleResponseException { ex -> Timber.e(ex) }
                }
            }
        }
    }

    /**
     * Binds the Kodein database.
     */
    private fun DI.MainBuilder.bindDatabase() = bindSingleton {
        DB.inDir(filesDir.absolutePath).open(
            "Gw2Database",
            KotlinxSerializer(Modules.ALL),
            IdentifiableMetadataExtractor(),
            TileMetadataExtractor(),
            TypeTable { gw2() }
        )
    }

    /**
     * Binds the GW2 providers.
     */
    private fun DI.MainBuilder.bindGw2() {
        bindSingleton { Gw2Client(instance(), configuration = Gw2ClientConfiguration(exceptionRecoveryMode = ExceptionRecoveryMode.DEFAULT)) }
        bindSingleton { Gw2CacheProvider(instance()).apply { inject(instance()) } }
        bindSingleton { TileClient(instance()) }
        bindSingleton { TileCache(instance(), instance()) }
        bindSingleton {
            EmblemClient(instance<HttpClient>().config {
                install(UserAgent) {
                    agent = EMBLEM_USER_AGENT
                }
            })
        }
    }

    /**
     * Binds the configuration.
     */
    @OptIn(ExperimentalXmlUtilApi::class)
    private fun DI.MainBuilder.bindConfiguration() = bindSingleton {
        try {
            // TODO attempt to get config from online location and default to bundled config if that fails
            val config = assets.open("Configuration.xml").bufferedReader(Charsets.UTF_8).use { reader -> reader.readText() }
            XML(serializersModule = SerializersModule {}) {
                this.unknownChildHandler = UnknownChildHandler { input, inputKind, descriptor, name, candidates ->
                    Timber.w("Unable to deserialize an unknown child of $name as $inputKind")
                    emptyList()
                }
            }.decodeFromString(Configuration.serializer(), config)
        } catch (ex: Exception) {
            Timber.e(ex, "Unable to create the configuration.")
            Configuration()
        }
    }

    /**
     * Binds the Coil image loader.
     */
    private fun DI.MainBuilder.bindImageLoader() = bindSingleton {
        ImageLoader.Builder(this@AppInitializer).okHttpClient(instance<OkHttpClient>()).build()
    }
}