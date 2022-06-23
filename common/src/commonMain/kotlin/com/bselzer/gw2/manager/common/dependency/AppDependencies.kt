package com.bselzer.gw2.manager.common.dependency

import com.bselzer.gw2.asset.cdn.client.AssetCdnClient
import com.bselzer.gw2.manager.BuildKonfig
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.configuration.Configuration
import com.bselzer.gw2.manager.common.preference.CommonPreference
import com.bselzer.gw2.manager.common.preference.WvwPreference
import com.bselzer.gw2.manager.common.repository.instance.Repositories
import com.bselzer.gw2.manager.common.repository.instance.generic.*
import com.bselzer.gw2.manager.common.repository.instance.specialized.SelectedWorldRepository
import com.bselzer.gw2.v2.cache.type.gw2
import com.bselzer.gw2.v2.client.instance.ExceptionRecoveryMode
import com.bselzer.gw2.v2.client.instance.Gw2Client
import com.bselzer.gw2.v2.client.instance.Gw2ClientConfiguration
import com.bselzer.gw2.v2.emblem.client.EmblemClient
import com.bselzer.gw2.v2.intl.cache.metadata.TranslationMetadataExtractor
import com.bselzer.gw2.v2.model.serialization.Modules
import com.bselzer.gw2.v2.tile.cache.metadata.TileGridMetadataExtractor
import com.bselzer.gw2.v2.tile.cache.metadata.TileMetadataExtractor
import com.bselzer.gw2.v2.tile.client.TileClient
import com.bselzer.ktx.compose.image.cache.metadata.ImageMetadataExtractor
import com.bselzer.ktx.compose.image.client.ImageClient
import com.bselzer.ktx.kodein.db.metadata.IdentifiableMetadataExtractor
import com.bselzer.ktx.kodein.db.value.IdentifierValueConverter
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.resource.assets.AssetReader
import com.bselzer.ktx.serialization.xml.configuration.LoggingUnknownChildHandler
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.SuspendSettings
import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.serializer
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.XML
import org.kodein.db.DB
import org.kodein.db.TypeTable
import org.kodein.db.impl.inDir
import org.kodein.db.kv.FailOnBadClose
import org.kodein.db.kv.TrackClosableAllocation
import org.kodein.db.orm.kotlinx.KotlinxSerializer

typealias DatabaseDirectory = String
typealias IsDebug = Boolean

interface AppDependencies {
    val build: BuildKonfig
    val clients: Clients
    val configuration: Configuration
    val database: DB
    val isDebug: IsDebug
    val libraries: List<Library>
    val preferences: Preferences
    val repositories: Repositories
    val scope: CoroutineScope

    fun initialize()
}

@OptIn(ExperimentalSettingsApi::class)
@Singleton
@Component
abstract class SingletonAppDependencies(
    private val debugMode: IsDebug,

    /**
     * The scope of the application's lifecycle.
     */
    private val lifecycleScope: CoroutineScope,

    /**
     * The location of the database.
     */
    @get:Provides protected val databaseDirectory: DatabaseDirectory,

    /**
     * The HTTP client for making network requests.
     */
    @get:Provides protected val httpClient: HttpClient,

    /**
     * The preference settings.
     */
    @get:Provides protected val settings: SuspendSettings,
) : AppDependencies {
    override fun initialize() {
        Logger.clear()

        // Only enable logging for debug mode.
        if (isDebug) {
            Logger.enableDebugging()
        }
    }

    @Singleton
    @Provides
    fun scope(): CoroutineScope = lifecycleScope

    @Singleton
    @Provides
    @Inject
    fun debugMode(build: BuildKonfig): IsDebug = debugMode || build.DEBUG

    @Singleton
    @Provides
    fun buildKonfig(): BuildKonfig = BuildKonfig

    @OptIn(ExperimentalXmlUtilApi::class)
    @Singleton
    @Provides
    fun configuration(): Configuration = with(AssetReader) {
        try {
            // TODO attempt to get config from online location and default to bundled config if that fails
            val content = AppResources.assets.Configuration.readText()
            XML {
                this.unknownChildHandler = LoggingUnknownChildHandler()
            }.decodeFromString(serializer(), content)
        } catch (ex: Exception) {
            Logger.e(ex, "Unable to create the configuration.")
            Configuration()
        }
    }

    @Singleton
    @Provides
    @Inject
    fun clients(httpClient: HttpClient): Clients = Clients(
        http = httpClient,
        gw2 = Gw2Client(
            httpClient = httpClient,
            configuration = Gw2ClientConfiguration(exceptionRecoveryMode = ExceptionRecoveryMode.DEFAULT)
        ),
        tile = TileClient(httpClient),
        image = ImageClient(httpClient),
        emblem = EmblemClient(httpClient),
        asset = AssetCdnClient(httpClient)
    )

    @Singleton
    @Provides
    @Inject
    fun database(
        databaseDirectory: DatabaseDirectory,
        isDebug: IsDebug
    ): DB = DB.inDir(databaseDirectory).open(
        path = "Gw2Database",

        // Add options related to GW2 models.
        KotlinxSerializer(Modules.ALL),
        IdentifiableMetadataExtractor(),
        TileMetadataExtractor(),
        TileGridMetadataExtractor(),
        TranslationMetadataExtractor(),
        ImageMetadataExtractor(),
        IdentifierValueConverter(),
        TypeTable { gw2() },

        // Add general database options.
        TrackClosableAllocation(isDebug),
        FailOnBadClose(isDebug),
    )

    @Singleton
    @Provides
    fun libraries(): List<Library> = with(AssetReader) {
        val content = AppResources.assets.aboutlibraries.readText()
        Libs.Builder().withJson(content).build().libraries
    }

    @OptIn(ExperimentalSettingsApi::class)
    @Singleton
    @Provides
    @Inject
    fun preferences(settings: SuspendSettings, configuration: Configuration): Preferences = Preferences(
        settings = settings,
        common = CommonPreference(settings),
        wvw = WvwPreference(settings, configuration)
    )

    @Singleton
    @Provides
    @Inject
    fun repositoryDependencies(
        clients: Clients,
        configuration: Configuration,
        database: DB,
        preferences: Preferences,
        scope: CoroutineScope
    ): RepositoryDependencies = object : RepositoryDependencies {
        override val clients: Clients = clients
        override val configuration: Configuration = configuration
        override val database: DB = database
        override val preferences: Preferences = preferences
        override val scope: CoroutineScope = scope
    }

    @Singleton
    @Provides
    @Inject
    fun repositories(
        color: ColorRepository,
        continent: ContinentRepository,
        guild: GuildRepository,
        image: ImageRepository,
        owner: OwnerRepository,
        tile: TileRepository,
        translation: TranslationRepository,
        world: WorldRepository,
        selectedWorld: SelectedWorldRepository,
    ): Repositories = object : Repositories {
        override val color: ColorRepository = color
        override val continent: ContinentRepository = continent
        override val guild: GuildRepository = guild
        override val image: ImageRepository = image
        override val owner: OwnerRepository = owner
        override val tile: TileRepository = tile
        override val translation: TranslationRepository = translation
        override val world: WorldRepository = world
        override val selectedWorld: SelectedWorldRepository = selectedWorld
    }
}