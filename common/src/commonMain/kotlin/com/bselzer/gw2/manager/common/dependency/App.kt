package com.bselzer.gw2.manager.common.dependency

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.bselzer.gw2.asset.cdn.client.AssetCdnClient
import com.bselzer.gw2.manager.common.configuration.Configuration
import com.bselzer.gw2.manager.common.preference.CommonPreference
import com.bselzer.gw2.manager.common.preference.WvwPreference
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
import com.bselzer.ktx.compose.image.ui.LocalImageCache
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.settings.compose.safeState
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.SuspendSettings
import io.ktor.client.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.UnknownChildHandler
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
     * The content of the bundled configuration file.
     */
    configurationContent: String,

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
    settings: SuspendSettings
) : Dependencies {
    override val preferences = Preferences(
        common = CommonPreference(settings),
        wvw = WvwPreference(settings)
    )

    override val configuration: Configuration = run {
        try {
            // TODO attempt to get config from online location and default to bundled config if that fails
            XML(serializersModule = SerializersModule {}) {
                this.unknownChildHandler = UnknownChildHandler { input, inputKind, descriptor, name, candidates ->
                    Logger.w("Unable to deserialize an unknown child of $name as $inputKind")
                    emptyList()
                }
            }.decodeFromString(serializer(), configurationContent)
        } catch (ex: Exception) {
            Logger.e(ex, "Unable to create the configuration.")
            Configuration()
        }
    }

    override val clients = Clients(
        gw2 = Gw2Client(
            httpClient = httpClient,
            configuration = Gw2ClientConfiguration(exceptionRecoveryMode = ExceptionRecoveryMode.DEFAULT)
        ),
        tile = TileClient(httpClient),
        emblem = EmblemClient(httpClient),
        asset = AssetCdnClient(httpClient)
    )

    override val caches: Caches = run {
        val db = DB.inDir(databaseDirectory).open(
            "Gw2Database",
            KotlinxSerializer(Modules.ALL),
            IdentifiableMetadataExtractor(),
            TileMetadataExtractor(),
            ImageMetadataExtractor(),
            TypeTable { gw2() }
        )

        val provider = Gw2CacheProvider(db).apply { inject(clients.gw2) }
        Caches(
            database = db,
            gw2 = provider,
            tile = TileCache(transactionManager = provider, client = clients.tile),
            image = ImageCache(transactionManager = provider, client = httpClient)
        )
    }

    fun initialize() {
        Logger.clear()

        // Only enable logging for debug mode.
        if (isDebug) {
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