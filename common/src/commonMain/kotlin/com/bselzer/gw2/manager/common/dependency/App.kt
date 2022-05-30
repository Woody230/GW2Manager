package com.bselzer.gw2.manager.common.dependency

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.bselzer.gw2.asset.cdn.client.AssetCdnClient
import com.bselzer.gw2.manager.BuildKonfig
import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.configuration.Configuration
import com.bselzer.gw2.manager.common.preference.CommonPreference
import com.bselzer.gw2.manager.common.preference.WvwPreference
import com.bselzer.gw2.manager.common.repository.instance.Repositories
import com.bselzer.gw2.manager.common.ui.theme.AppTheme
import com.bselzer.gw2.v2.cache.type.gw2
import com.bselzer.gw2.v2.client.instance.ExceptionRecoveryMode
import com.bselzer.gw2.v2.client.instance.Gw2Client
import com.bselzer.gw2.v2.client.instance.Gw2ClientConfiguration
import com.bselzer.gw2.v2.emblem.client.EmblemClient
import com.bselzer.gw2.v2.model.serialization.Modules
import com.bselzer.gw2.v2.tile.cache.metadata.TileGridMetadataExtractor
import com.bselzer.gw2.v2.tile.cache.metadata.TileMetadataExtractor
import com.bselzer.gw2.v2.tile.client.TileClient
import com.bselzer.ktx.compose.image.cache.metadata.ImageMetadataExtractor
import com.bselzer.ktx.compose.image.client.ImageClient
import com.bselzer.ktx.compose.ui.intl.LocalLocale
import com.bselzer.ktx.compose.ui.intl.ProvideLocale
import com.bselzer.ktx.kodein.db.metadata.IdentifiableMetadataExtractor
import com.bselzer.ktx.kodein.db.value.IdentifierValueConverter
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.resource.assets.AssetReader
import com.bselzer.ktx.serialization.xml.configuration.LoggingUnknownChildHandler
import com.bselzer.ktx.settings.compose.safeState
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.SuspendSettings
import io.ktor.client.*
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.XML
import org.kodein.db.DB
import org.kodein.db.TypeTable
import org.kodein.db.impl.inDir
import org.kodein.db.kv.FailOnBadClose
import org.kodein.db.kv.TrackClosableAllocation
import org.kodein.db.orm.kotlinx.KotlinxSerializer

@OptIn(ExperimentalSettingsApi::class, ExperimentalXmlUtilApi::class)
abstract class App(
    /**
     * Whether debug mode is enabled.
     */
    debug: Boolean = false,

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

    final override val isDebug: Boolean = debug || build.DEBUG

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

    final override val database: DB = DB.inDir(databaseDirectory).open(
        "Gw2Database",

        // Add options related to GW2 models.
        KotlinxSerializer(Modules.ALL),
        IdentifiableMetadataExtractor(),
        TileMetadataExtractor(),
        TileGridMetadataExtractor(),
        ImageMetadataExtractor(),
        IdentifierValueConverter(),
        TypeTable { gw2() },

        // Add general database options.
        TrackClosableAllocation(isDebug),
        FailOnBadClose(isDebug),
    )

    // TODO dependency injection?
    final override val repositories: Repositories = run {
        val dependencies = object : RepositoryDependencies {
            override val clients = this@App.clients
            override val configuration = this@App.configuration
            override val database = this@App.database
            override val preferences = this@App.preferences
        }

        val generic = GenericRepositories(dependencies)
        SpecializedRepositories(dependencies, generic)
    }

    fun initialize() {
        Logger.clear()

        // Only enable logging for debug mode.
        if (isDebug) {
            Logger.enableDebugging()
        }
    }

    @Composable
    fun Content(content: @Composable () -> Unit) = AppTheme(
        theme = preferences.common.theme.safeState().value,
    ) {
        // Update the locale as it gets changed and recompose.
        ProvideLocale {
            val locale = LocalLocale.current
            Logger.d { "Locale | $locale" }

            CompositionLocalProvider(
                LocalDependencies provides this,
                content = content
            )
        }
    }
}