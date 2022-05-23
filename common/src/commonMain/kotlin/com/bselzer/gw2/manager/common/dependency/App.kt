package com.bselzer.gw2.manager.common.dependency

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.bselzer.gw2.asset.cdn.client.AssetCdnClient
import com.bselzer.gw2.manager.BuildKonfig
import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.configuration.Configuration
import com.bselzer.gw2.manager.common.preference.CommonPreference
import com.bselzer.gw2.manager.common.preference.WvwPreference
import com.bselzer.gw2.manager.common.repository.WorldRepository
import com.bselzer.gw2.manager.common.repository.WvwRepository
import com.bselzer.gw2.manager.common.ui.theme.AppTheme
import com.bselzer.gw2.v2.cache.metadata.IdentifiableMetadataExtractor
import com.bselzer.gw2.v2.cache.provider.Gw2CacheProvider
import com.bselzer.gw2.v2.cache.type.gw2
import com.bselzer.gw2.v2.client.client.ExceptionRecoveryMode
import com.bselzer.gw2.v2.client.client.Gw2Client
import com.bselzer.gw2.v2.client.client.Gw2ClientConfiguration
import com.bselzer.gw2.v2.emblem.client.EmblemClient
import com.bselzer.gw2.v2.model.serialization.Modules
import com.bselzer.gw2.v2.tile.cache.instance.TileCache
import com.bselzer.gw2.v2.tile.cache.metadata.TileMetadataExtractor
import com.bselzer.gw2.v2.tile.client.TileClient
import com.bselzer.ktx.compose.image.cache.instance.ImageCache
import com.bselzer.ktx.compose.image.cache.metadata.ImageMetadataExtractor
import com.bselzer.ktx.compose.image.client.ImageClient
import com.bselzer.ktx.compose.image.ui.LocalImageCache
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.resource.assets.AssetReader
import com.bselzer.ktx.serialization.xml.configuration.LoggingUnknownChildHandler
import com.bselzer.ktx.settings.compose.safeState
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.SuspendSettings
import io.ktor.client.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.XML
import org.kodein.db.DB
import org.kodein.db.TypeTable
import org.kodein.db.impl.inDir
import org.kodein.db.orm.kotlinx.KotlinxSerializer

@OptIn(ExperimentalSettingsApi::class, ExperimentalXmlUtilApi::class)
abstract class App(
    /**
     * Whether debug mode is enabled.
     */
    private val isDebug: Boolean = false,

    /**
     * The HTTP client for making network requests.
     */
    httpClient: HttpClient,

    /**
     * The location of the database.
     */
    databaseDirectory: String,

    /**
     * The preference settings.
     */
    settings: SuspendSettings,
) : Dependencies {
    final override val build: BuildKonfig = BuildKonfig

    final override val preferences = Preferences(
        common = CommonPreference(settings),
        wvw = WvwPreference(settings)
    )

    final override val libraries: List<Library> = with(AssetReader) {
        val content = Gw2Resources.assets.aboutlibraries.readText()
        Libs.Builder().withJson(content).build().libraries
    }

    final override val configuration: Configuration = with(AssetReader) {
        try {
            // TODO attempt to get config from online location and default to bundled config if that fails
            val content = Gw2Resources.assets.Configuration.readText()
            XML {
                this.unknownChildHandler = LoggingUnknownChildHandler()
            }.decodeFromString(serializer(), content)
        } catch (ex: Exception) {
            Logger.e(ex, "Unable to create the configuration.")
            Configuration()
        }
    }

    final override val clients = Clients(
        gw2 = Gw2Client(
            httpClient = httpClient,
            configuration = Gw2ClientConfiguration(exceptionRecoveryMode = ExceptionRecoveryMode.DEFAULT)
        ),
        tile = TileClient(httpClient),
        image = ImageClient(httpClient),
        emblem = EmblemClient(httpClient),
        asset = AssetCdnClient(httpClient)
    )

    final override val caches: Caches = Caches(
        gw2 = Gw2CacheProvider(clients.gw2),
        tile = TileCache(clients.tile),
        image = ImageCache(clients.image),
        database = DB.inDir(databaseDirectory).open(
            "Gw2Database",
            KotlinxSerializer(Modules.ALL),
            IdentifiableMetadataExtractor(),
            TileMetadataExtractor(),
            ImageMetadataExtractor(),
            TypeTable { gw2() }
        )
    )

    final override val lock: Mutex = Mutex()

    final override val repositories: Repositories
        get() {
            val dependencies = object : RepositoryDependencies {
                override val lock: Mutex = this@App.lock
                override val caches: Caches = this@App.caches
                override val clients: Clients = this@App.clients
                override val configuration: Configuration = this@App.configuration
                override val preferences: Preferences = this@App.preferences
            }

            return Repositories(
                world = WorldRepository(dependencies),
                wvw = WvwRepository(dependencies)
            )
        }

    fun initialize() {
        Logger.clear()

        // Only enable logging for debug mode.
        if (isDebug || build.DEBUG) {
            Logger.enableDebugging()
        }
    }

    @Composable
    fun Content(content: @Composable () -> Unit) = CompositionLocalProvider(
        LocalImageCache provides caches.image,
    ) {
        AppTheme(
            theme = preferences.common.theme.safeState().value,
            content = content
        )
    }
}