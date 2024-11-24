package com.bselzer.gw2.manager.common.dependency

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.bselzer.gw2.asset.cdn.client.AssetCdnClient
import com.bselzer.gw2.manager.BuildKonfig
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.configuration.AppConfiguration
import com.bselzer.gw2.manager.common.configuration.Configuration
import com.bselzer.gw2.manager.common.preference.CommonPreference
import com.bselzer.gw2.manager.common.preference.WvwPreference
import com.bselzer.gw2.manager.common.repository.instance.Repositories
import com.bselzer.gw2.manager.common.repository.instance.generic.*
import com.bselzer.gw2.manager.common.repository.instance.specialized.MapRepository
import com.bselzer.gw2.manager.common.repository.instance.specialized.SelectedWorldRepository
import com.bselzer.gw2.manager.common.repository.instance.specialized.WvwMatchRepository
import com.bselzer.gw2.v2.client.instance.ExceptionRecoveryMode
import com.bselzer.gw2.v2.client.instance.Gw2Client
import com.bselzer.gw2.v2.client.instance.Gw2ClientConfiguration
import com.bselzer.gw2.v2.client.instance.TileClient
import com.bselzer.gw2.v2.emblem.client.EmblemClient
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.resource.assets.AssetReader
import com.bselzer.ktx.serialization.LoggingUnknownChildHandler
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.SuspendSettings
import io.github.irgaly.kottage.Kottage
import io.github.irgaly.kottage.KottageEnvironment
import io.github.irgaly.kottage.platform.KottageLogger
import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.serialization.DefaultXmlSerializationPolicy
import nl.adaptivity.xmlutil.serialization.XML

typealias DatabaseDirectory = String
typealias IsDebug = Boolean

interface AppDependencies {
    val build: BuildKonfig
    val clients: Clients
    val configuration: Configuration
    val isDebug: IsDebug
    val legacyDatabaseDirectory: DatabaseDirectory
    val libraries: List<Library>
    val preferences: Preferences
    val storage: Storage
    val repositories: Repositories
    val imageLoader: ImageLoader
    val scope: CoroutineScope
    val kottage: Kottage

    fun initialize()
}

@OptIn(ExperimentalSettingsApi::class)
class SingletonAppDependencies(
    debugMode: IsDebug,

    /**
     * The scope of the application's lifecycle.
     */
    lifecycleScope: CoroutineScope,

    /**
     * The location of the database.
     */
    databaseDirectory: DatabaseDirectory,

    /**
     * The location of the Kodein DB database.
     */
    override val legacyDatabaseDirectory: DatabaseDirectory,

    /**
     * The HTTP client for making network requests.
     */
    httpClient: HttpClient,

    /**
     * The preference settings.
     */
    settings: SuspendSettings,

    /**
     * The Coil platform context.
     */
    coilContext: PlatformContext,

    /**
     * The Kottage environment.
     */
    kottageEnvironment: KottageEnvironment,
) : AppDependencies {
    override val build = BuildKonfig
    override val isDebug = debugMode || build.DEBUG
    override val clients = clients(httpClient)
    override val scope = lifecycleScope
    override val configuration = configuration()
    override val libraries = libraries()
    override val preferences = preferences(settings, configuration)
    override val imageLoader: ImageLoader = SingletonImageLoader.get(coilContext)
    override val kottage = kottage(databaseDirectory, kottageEnvironment, scope)
    override val storage = Storage(kottage)

    private val repositoryDependencies = repositoryDependencies(clients, configuration, storage, preferences, scope)
    private val colorRepository = ColorRepository(repositoryDependencies)
    private val translationRepository = TranslationRepository(repositoryDependencies)
    private val continentRepository = ContinentRepository(
        repositoryDependencies,
        ContinentRepository.Repositories(translationRepository)
    )
    private val guildRepository = GuildRepository(
        repositoryDependencies,
        GuildRepository.Repositories(translationRepository)
    )
    private val ownerRepository = OwnerRepository(repositoryDependencies)
    private val statusRepository = StatusRepository(repositoryDependencies)
    private val tileRepository = TileRepository(repositoryDependencies)
    private val worldRepository = WorldRepository(
        repositoryDependencies,
        WorldRepository.Repositories(translationRepository)
    )
    private val mapRepository = MapRepository(
        repositoryDependencies,
        MapRepository.Repositories(continentRepository, tileRepository)
    )
    private val matchRepository = WvwMatchRepository(
        repositoryDependencies,
        WvwMatchRepository.Repositories(guildRepository, ownerRepository, translationRepository, worldRepository)
    )
    private val selectedWorldRepository = SelectedWorldRepository(
        repositoryDependencies,
        SelectedWorldRepository.Repositories(mapRepository, matchRepository, worldRepository, statusRepository, translationRepository)
    )

    override val repositories: Repositories = repositories(
        colorRepository,
        continentRepository,
        guildRepository,
        ownerRepository,
        statusRepository,
        tileRepository,
        translationRepository,
        worldRepository,
        selectedWorldRepository
    )

    override fun initialize() {
        Logger.clear()

        // Only enable logging for debug mode.
        if (isDebug) {
            Logger.enableDebugging()
        }
    }

    @OptIn(ExperimentalXmlUtilApi::class)
    fun configuration(): Configuration = with(AssetReader) {
        try {
            // TODO attempt to get config from online location and default to bundled config if that fails
            val content = AppResources.assets.Configuration_xml.readText()
            XML {
                policy = DefaultXmlSerializationPolicy.Builder().apply {
                    pedantic = false
                    unknownChildHandler = LoggingUnknownChildHandler()
                }.build()
            }.decodeFromString(serializer<AppConfiguration>(), content)
        } catch (ex: Exception) {
            Logger.e(ex, "Unable to create the configuration.")
            AppConfiguration()
        }
    }

    fun clients(httpClient: HttpClient): Clients = Clients(
        http = httpClient,
        gw2 = Gw2Client(
            httpClient = httpClient,
            configuration = Gw2ClientConfiguration(exceptionRecoveryMode = ExceptionRecoveryMode.DEFAULT)
        ),
        tile = TileClient(httpClient),
        emblem = EmblemClient(httpClient),
        asset = AssetCdnClient(httpClient)
    )

    fun kottage(
        databaseDirectory: DatabaseDirectory,
        environment: KottageEnvironment,
        scope: CoroutineScope
    ) = Kottage(
        name = "KottageDatabase",
        directoryPath = databaseDirectory,
        environment = environment.copy(
            logger = object : KottageLogger {
                override suspend fun debug(message: String) {
                    Logger.d { "Kottage | $message" }
                }
                override suspend fun error(message: String) {
                    Logger.e { "Kottage | $message" }
                }
            }
        ),
        scope = scope
    )

    fun libraries(): List<Library> = with(AssetReader) {
        val content = AppResources.assets.aboutlibraries_json.readText()
        Libs.Builder().withJson(content).build().libraries
    }

    @OptIn(ExperimentalSettingsApi::class)
    fun preferences(settings: SuspendSettings, configuration: Configuration): Preferences = Preferences(
        settings = settings,
        common = CommonPreference(settings),
        wvw = WvwPreference(settings, configuration)
    )

    fun repositoryDependencies(
        clients: Clients,
        configuration: Configuration,
        storage: Storage,
        preferences: Preferences,
        scope: CoroutineScope
    ): RepositoryDependencies = object : RepositoryDependencies {
        override val clients: Clients = clients
        override val configuration: Configuration = configuration
        override val storage: Storage = storage
        override val preferences: Preferences = preferences
        override val scope: CoroutineScope = scope
    }

    fun repositories(
        color: ColorRepository,
        continent: ContinentRepository,
        guild: GuildRepository,
        owner: OwnerRepository,
        status: StatusRepository,
        tile: TileRepository,
        translation: TranslationRepository,
        world: WorldRepository,
        selectedWorld: SelectedWorldRepository,
    ): Repositories = object : Repositories {
        override val color: ColorRepository = color
        override val continent: ContinentRepository = continent
        override val guild: GuildRepository = guild
        override val owner: OwnerRepository = owner
        override val status: StatusRepository = status
        override val tile: TileRepository = tile
        override val translation: TranslationRepository = translation
        override val world: WorldRepository = world
        override val selectedWorld: SelectedWorldRepository = selectedWorld
    }
}