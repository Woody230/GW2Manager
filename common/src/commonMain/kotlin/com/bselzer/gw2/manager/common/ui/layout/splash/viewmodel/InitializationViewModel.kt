package com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.splash.model.initialization.Descriptor
import com.bselzer.gw2.manager.common.ui.layout.splash.model.initialization.Initializer
import com.bselzer.gw2.manager.common.ui.layout.splash.model.initialization.migration.Migrator
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.plus

class InitializationViewModel(
    context: AppComponentContext,
) : SplashViewModel(context) {
    @Composable
    fun Initialize(onFinish: () -> Unit) {
        val initializers = initializers
        LaunchedEffect(true) {
            initializers.forEach { initializer ->
                description.value = initializer.descriptor
                initializer.block()
            }

            onFinish()
        }
    }

    private val noDescription = Descriptor(title = "...".desc(), subtitle = null)
    val description: MutableState<Descriptor> = mutableStateOf(noDescription)

    // TODO add build number if needed
    private val initializers: Collection<Initializer>
        @Composable
        get() = listOf(initializeLanguage, initializeTheme, migration, initializeWvwRefresh)

    private val initializeLanguage
        get() = Initializer(
            title = KtxResources.strings.settings.desc(),
            subtitle = KtxResources.strings.language.desc()
        ) {
            repositories.translation.updateLocale(preferences.common.locale.get())
        }

    private val initializeTheme
        @Composable
        get() = run {
            val initialTheme = if (isSystemInDarkTheme()) Theme.DARK else Theme.LIGHT
            Initializer(
                title = KtxResources.strings.settings.desc(),
                subtitle = KtxResources.strings.theme.desc(),
            ) {
                preferences.common.theme.initialize(initialTheme)
            }
        }

    private val migration
        get() = run {
            val newVersion = build.VERSION_CODE
            Initializer(
                title = KtxResources.strings.migration.desc(),
                subtitle = KtxResources.strings.version.desc() + " $newVersion".desc()
            ) {
                val currentVersion = preferences.common.appVersion.get()
                Logger.d { "Migration | Current version $currentVersion | New version $newVersion" }
                try {
                    Migrator(this).migrate(currentVersion)
                    preferences.common.appVersion.set(newVersion)
                } catch (ex: Exception) {
                    Logger.e(ex) { "Failed to perform the migration from $currentVersion to $newVersion" }
                }
            }
        }

    private val initializeBuildNumber
        get() = Initializer(
            title = AppResources.strings.build_number.desc(),
            subtitle = null,
        ) {
            // Build number has been static for months https://github.com/gw2-api/issues/issues/1 so assetcdn must be used
            var newId = clients.asset.latest().id
            if (newId <= 0) {
                Logger.w("Asset CDN build id is not valid. Defaulting to the api.")
                newId = clients.gw2.build.buildId()
            }

            val buildNumber = preferences.common.buildNumber
            val oldId = buildNumber.get()
            Logger.d("Build Number | Old build id $oldId | New build id $newId")
            if (newId > oldId) {
                buildNumber.set(newId.value)
            }
        }

    private val initializeWvwRefresh
        get() = Initializer(
            title = AppResources.strings.wvw.desc(),
            subtitle = KtxResources.strings.refresh.desc()
        ) {
            // The normal refresh will wait forever when checking against the initial value (the distant future).
            // Therefore we need to make sure we have actually refreshed once to provide an actual date.
            if (!preferences.wvw.lastRefresh.exists()) {
                repositories.selectedWorld.forceRefresh()
            }
        }
}