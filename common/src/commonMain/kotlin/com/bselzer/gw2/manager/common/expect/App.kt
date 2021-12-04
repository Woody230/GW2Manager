package com.bselzer.gw2.manager.common.expect

import androidx.compose.runtime.*
import com.bselzer.gw2.manager.common.configuration.Configuration
import com.bselzer.gw2.manager.common.preference.CommonPreference
import com.bselzer.gw2.manager.common.preference.WvwPreference
import com.bselzer.gw2.manager.common.ui.theme.AppTheme
import com.bselzer.gw2.manager.common.ui.theme.Theme
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
import com.bselzer.library.kotlin.extension.logging.Logger
import com.russhwolf.settings.ExperimentalSettingsApi
import io.ktor.client.*
import io.ktor.client.features.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.UnknownChildHandler
import nl.adaptivity.xmlutil.serialization.XML
import org.kodein.db.DB
import org.kodein.db.TypeTable
import org.kodein.db.impl.inDir
import org.kodein.db.orm.kotlinx.KotlinxSerializer
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton
import org.kodein.di.instance

abstract class App : DIAware {
    final override val di: DI = DI.lazy { bindAll() }
    private val database by instance<DB>()
    private val configuration by instance<Configuration>()
    private val commonPref by instance<CommonPreference>()

    companion object {
        /**
         * The name of the GW2 user agent.
         */
        const val GW2_USER_AGENT: String = "gw2"

        /**
         * The name of the GW2 emblem user agent.
         */
        const val EMBLEM_USER_AGENT: String = "gw2-emblem"

        /**
         * The initial data population flag.
         */
        const val INITIAL_DATA_POPULATION: String = "InitialDataPopulation"
    }

    /**
     * Whether debug mode is enabled.
     */
    abstract val isDebug: Boolean

    /**
     * Initializes the application.
     */
    fun initialize() {
        Logger.clear()

        // Only enable logging for debug mode.
        if (isDebug) {
            Logger.enableDebugging()
        }
    }

    /**
     * @return the current app theme type, defaulting to [Theme.DARK]
     */
    @Composable
    fun theme(): Theme {
        // Using runBlocking to avoid the initial theme changing because it is noticeable.
        val initial = runBlocking { commonPref.theme.get() }
        val theme by commonPref.theme.observe().collectAsState(initial = initial)
        return theme
    }

    @Composable
    fun Content(content: @Composable () -> Unit) = AppTheme(content)

    /**
     * Binds all the dependencies.
     */
    private fun DI.MainBuilder.bindAll() {
        bindHttpClient()
        bindSettings()
        bindPlatform()
        bindDatabase()
        bindPreferences()
        bindConfiguration()
        bindGw2()
        bindSingleton(tag = INITIAL_DATA_POPULATION) { mutableStateOf(false) }
    }

    /**
     * Binds the HTTP client.
     */
    protected abstract fun DI.MainBuilder.bindHttpClient()

    /**
     * @return the bundled configuration content as a String
     */
    protected abstract fun DI.MainBuilder.bundledConfiguration(): String

    /**
     * @return the GW2 database directory
     */
    protected abstract fun DI.MainBuilder.databaseDirectory(): String

    /**
     * Binds the settings.
     */
    protected abstract fun DI.MainBuilder.bindSettings()

    /**
     * Binds the platform specific dependencies.
     */
    protected abstract fun DI.MainBuilder.bindPlatform()

    /**
     * Binds the Kodein database.
     */
    private fun DI.MainBuilder.bindDatabase() = bindSingleton {
        DB.inDir(databaseDirectory()).open(
            "Gw2Database",
            KotlinxSerializer(Modules.ALL),
            IdentifiableMetadataExtractor(),
            TileMetadataExtractor(),
            TypeTable { gw2() }
        )
    }

    @OptIn(ExperimentalSettingsApi::class)
    private fun DI.MainBuilder.bindPreferences() {
        bindSingleton { CommonPreference(instance()) }
        bindSingleton { WvwPreference(instance()) }
    }

    /**
     * Binds the configuration.
     */
    @OptIn(ExperimentalXmlUtilApi::class)
    private fun DI.MainBuilder.bindConfiguration() = bindSingleton {
        try {
            // TODO attempt to get config from online location and default to bundled config if that fails
            val config = bundledConfiguration()
            XML(serializersModule = SerializersModule {}) {
                this.unknownChildHandler = UnknownChildHandler { input, inputKind, descriptor, name, candidates ->
                    Logger.w("Unable to deserialize an unknown child of $name as $inputKind")
                    emptyList()
                }
            }.decodeFromString(serializer(), config)
        } catch (ex: Exception) {
            Logger.e(ex, "Unable to create the configuration.")
            Configuration()
        }
    }

    /**
     * Binds the GW2 providers.
     */
    private fun DI.MainBuilder.bindGw2() {
        // TODO try wrappers around cache/clients
        bindSingleton { Gw2Client(instance(), configuration = Gw2ClientConfiguration(exceptionRecoveryMode = ExceptionRecoveryMode.DEFAULT)) }
        bindSingleton { Gw2CacheProvider(instance()).apply { inject(instance()) } }
        bindSingleton { TileClient(instance()) }
        bindSingleton { TileCache(instance(), instance()) }
        bindSingleton {
            com.bselzer.library.gw2.v2.emblem.client.EmblemClient(instance<HttpClient>().config {
                install(UserAgent) {
                    agent = EMBLEM_USER_AGENT
                }
            })
        }
    }
}
