package com.bselzer.gw2.manager.common.dependency

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import app.cash.sqldelight.db.SqlDriver
import coil3.PlatformContext
import com.bselzer.gw2.manager.common.ui.theme.AppTheme
import com.bselzer.ktx.compose.ui.intl.LocalLocale
import com.bselzer.ktx.compose.ui.intl.ProvideLocale
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.settings.safeState
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.SuspendSettings
import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalSettingsApi::class)
abstract class App(
    /**
     * Whether debug mode is enabled.
     */
    debugMode: IsDebug = false,

    /**
     * The scope of the application's lifecycle.
     */
    scope: CoroutineScope,

    /**
     * The HTTP client for making network requests.
     */
    httpClient: HttpClient,

    /**
     * The location of the database.
     */
    databaseDirectory: DatabaseDirectory,

    /**
     * The SQL database driver.
     */
    sqlDriver: SqlDriver,

    /**
     * The preference settings.
     */
    settings: SuspendSettings,

    /**
     * The Coil platform context.
     */
    platformContext: PlatformContext
) {
    val dependencies: AppDependencies = SingletonAppDependencies(
        debugMode = debugMode,
        lifecycleScope = scope,
        httpClient = httpClient,
        sqlDriver = sqlDriver,
        settings = settings,
        platformContext = platformContext
    )

    init {
        dependencies.initialize()
    }

    @Composable
    fun Content(content: @Composable () -> Unit) = AppTheme(
        theme = dependencies.preferences.common.theme.safeState().value,
    ) {
        // Update the locale as it gets changed and recompose.
        ProvideLocale {
            val locale = LocalLocale.current
            Logger.d { "Compose | Locale | $locale" }

            CompositionLocalProvider(
                LocalDependencies provides dependencies,
                content = content
            )
        }
    }
}