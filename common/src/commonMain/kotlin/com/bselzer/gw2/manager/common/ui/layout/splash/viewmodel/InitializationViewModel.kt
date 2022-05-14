package com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.splash.model.initialization.Initializer
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.compose.ui.layout.description.DescriptionInteractor
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.resource.Resources
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class InitializationViewModel(
    context: AppComponentContext,
) : SplashViewModel(context) {
    @Composable
    fun Initialize(onFinish: () -> Unit) {
        val initializers = initializers
        val descriptions = initializers.associateWith { initializer ->
            DescriptionInteractor(
                title = TextInteractor(initializer.title.localized()),
                subtitle = TextInteractor(initializer.subtitle.localized())
            )
        }

        LaunchedEffect(true) {
            initializers.forEach { initializer ->
                description.value = descriptions.getOrDefault(initializer, noDescription)
                initializer.block()
            }

            description.value = noDescription
            onFinish()
        }
    }

    private val noDescription = DescriptionInteractor(title = TextInteractor(""), subtitle = TextInteractor(""))
    val description: MutableState<DescriptionInteractor> = mutableStateOf(noDescription)

    // TODO add build number if needed
    private val initializers: Collection<Initializer>
        @Composable
        get() = listOf(initializePreferences)


    private val initialTheme
        @Composable
        get() = if (isSystemInDarkTheme()) Theme.DARK else Theme.LIGHT

    private val initializePreferences
        @Composable
        get() = run {
            val initialTheme = initialTheme
            Initializer(
                title = Resources.strings.settings.desc(),
                subtitle = StringDesc.Raw(""),
            ) {
                preferences.common.theme.initialize(initialTheme)
            }
        }

    private val initializeBuildNumber
        get() = Initializer(
            title = Gw2Resources.strings.build_number.desc(),
            subtitle = StringDesc.Raw(""),
        ) {
            // Build number has been static for months https://github.com/gw2-api/issues/issues/1 so assetcdn must be used
            var newId = clients.asset.latest().id
            if (newId <= 0) {
                Logger.w("Asset CDN build id is not valid. Defaulting to the api.")
                newId = clients.gw2.build.buildId()
            }

            val buildNumber = preferences.common.buildNumber
            val oldId = buildNumber.get()
            Logger.d("Old build id: $oldId | New build id: $newId")
            if (newId > oldId) {
                buildNumber.set(newId.value)
            }
        }
}