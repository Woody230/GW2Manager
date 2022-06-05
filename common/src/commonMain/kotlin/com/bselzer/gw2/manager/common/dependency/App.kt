package com.bselzer.gw2.manager.common.dependency

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.bselzer.gw2.manager.common.ui.theme.AppTheme
import com.bselzer.ktx.compose.ui.intl.LocalLocale
import com.bselzer.ktx.compose.ui.intl.ProvideLocale
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.settings.compose.safeState
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.SuspendSettings
import io.ktor.client.*
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi

@OptIn(ExperimentalSettingsApi::class, ExperimentalXmlUtilApi::class)
abstract class App(
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
) {
    val dependencies: AppDependencies = SingletonAppDependencies::class.create(
        httpClient = httpClient,
        databaseDirectory = databaseDirectory,
        settings = settings,
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