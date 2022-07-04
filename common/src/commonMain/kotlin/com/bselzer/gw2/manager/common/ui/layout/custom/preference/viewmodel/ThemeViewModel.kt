package com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.bselzer.gw2.manager.common.dependency.LocalTheme
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.theme.ThemeLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.theme.ThemeResources
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.ktx.resource.KtxResources
import com.bselzer.ktx.settings.setting.Setting
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch

class ThemeViewModel(
    context: AppComponentContext
) : ViewModel(context) {
    private val setting: Setting<Theme> = preferences.common.theme

    val resources: ThemeResources
        @Composable
        get() = ThemeResources(
            image = when (LocalTheme.current) {
                Theme.LIGHT -> Gw2Resources.images.sunrise
                Theme.DARK -> Gw2Resources.images.twilight
            },
            title = KtxResources.strings.theme.desc(),
            subtitle = when (LocalTheme.current) {
                Theme.LIGHT -> KtxResources.strings.light
                Theme.DARK -> KtxResources.strings.dark
            }.desc()
        )

    val logic: ThemeLogic
        @Composable
        get() = run {
            val scope = rememberCoroutineScope()
            ThemeLogic(
                checked = LocalTheme.current != Theme.LIGHT,
                onCheckedChange = { checked ->
                    scope.launch {
                        val theme = if (checked) Theme.DARK else Theme.LIGHT
                        setting.set(theme)
                    }
                }
            )
        }
}