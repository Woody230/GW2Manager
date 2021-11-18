package com.bselzer.gw2.manager.ui.activity

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import coil.ImageLoader
import coil.util.CoilUtils
import com.bselzer.gw2.manager.BuildConfig
import com.bselzer.gw2.manager.companion.preference.PreferenceCompanion
import com.bselzer.gw2.manager.companion.preference.WvwPreferenceCompanion
import com.bselzer.gw2.manager.configuration.Configuration
import com.bselzer.library.gw2.v2.cache.metadata.IdentifiableMetadataExtractor
import com.bselzer.library.gw2.v2.cache.provider.Gw2CacheProvider
import com.bselzer.library.gw2.v2.cache.type.gw2
import com.bselzer.library.gw2.v2.client.client.ExceptionRecoveryMode
import com.bselzer.library.gw2.v2.client.client.Gw2Client
import com.bselzer.library.gw2.v2.client.client.Gw2ClientConfiguration
import com.bselzer.library.gw2.v2.model.serialization.Modules
import com.bselzer.library.gw2.v2.tile.cache.instance.TileCache
import com.bselzer.library.gw2.v2.tile.cache.metadata.TileMetadataExtractor
import com.bselzer.library.gw2.v2.tile.client.TileClient
import com.bselzer.library.kotlin.extension.kodein.db.transaction.DBTransactionProvider
import com.bselzer.library.kotlin.extension.preference.initialize
import com.bselzer.library.kotlin.extension.preference.nullLatest
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.UnknownChildHandler
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.OkHttpClient
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

    private companion object {
        /**
         * The default preferences datastore.
         */
        private val Context.DATASTORE: DataStore<Preferences> by preferencesDataStore("default")
    }

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            // Default preferences.
            DATASTORE.initialize(WvwPreferenceCompanion.REFRESH_INTERVAL, 5)

            // Initialize the client with the token if it exists.
            val token = DATASTORE.nullLatest(PreferenceCompanion.TOKEN) ?: return@launch
            val client by di.instance<Gw2Client>()
            client.config { copy(token = token) }
            Timber.d("Set client token to $token")
        }
    }

    override val di: DI by DI.lazy {
        bindHttpClient()
        bindDatabase()
        bindTransactionProvider()
        bindGw2()
        bindConfiguration()
        bindImageLoader()
        bindSingleton { DATASTORE }
    }

    /**
     * Binds the HTTP client for Ktor and Coil.
     */
    private fun DI.MainBuilder.bindHttpClient() {
        bindSingleton { OkHttpClient.Builder().cache(CoilUtils.createDefaultCache(this@AppInitializer)).build() }
        bindSingleton {
            HttpClient(OkHttp) {
                engine {
                    // Set the bound OkHttpClient.
                    preconfigured = instance()
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
     * Binds the Kodein database transaction provider.
     */
    private fun DI.MainBuilder.bindTransactionProvider() = bindSingleton {
        // Use the bound database.
        DBTransactionProvider(instance())
    }

    /**
     * Binds the GW2 providers.
     */
    private fun DI.MainBuilder.bindGw2() {
        bindSingleton { Gw2Client(instance(), configuration = Gw2ClientConfiguration(exceptionRecoveryMode = ExceptionRecoveryMode.DEFAULT)) }
        bindSingleton { Gw2CacheProvider(instance(), instance()).apply { inject(instance()) } }
        bindSingleton { TileClient(instance()) }
        bindSingleton { TileCache(instance(), instance()) }
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