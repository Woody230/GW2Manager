package com.bselzer.gw2.manager.common.expect

import com.bselzer.gw2.asset.cdn.client.AssetCdnClient
import com.bselzer.gw2.manager.common.configuration.Configuration
import com.bselzer.gw2.manager.common.preference.CommonPreference
import com.bselzer.gw2.manager.common.preference.WvwPreference
import com.bselzer.gw2.v2.cache.metadata.IdentifiableMetadataExtractor
import com.bselzer.gw2.v2.cache.provider.Gw2CacheProvider
import com.bselzer.gw2.v2.cache.type.gw2
import com.bselzer.gw2.v2.client.client.ExceptionRecoveryMode
import com.bselzer.gw2.v2.client.client.Gw2Client
import com.bselzer.gw2.v2.client.client.Gw2ClientConfiguration
import com.bselzer.gw2.v2.model.serialization.Modules
import com.bselzer.gw2.v2.tile.cache.instance.TileCache
import com.bselzer.gw2.v2.tile.cache.metadata.TileMetadataExtractor
import com.bselzer.gw2.v2.tile.client.TileClient
import com.bselzer.ktx.compose.image.cache.instance.ImageCache
import com.bselzer.ktx.compose.image.cache.metadata.ImageMetadataExtractor
import com.bselzer.ktx.logging.Logger
import com.russhwolf.settings.ExperimentalSettingsApi
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
        bindImageLoader()
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
            ImageMetadataExtractor(),
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
        bindSingleton { com.bselzer.gw2.v2.emblem.client.EmblemClient(instance()) }
        bindSingleton { AssetCdnClient(instance()) }
    }

    /**
     * Binds the image loader.
     */
    private fun DI.MainBuilder.bindImageLoader() = bindSingleton { ImageCache(instance(), instance()) }
}
