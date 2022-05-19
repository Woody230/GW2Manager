package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.dependency.LocalTheme
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.main.model.settings.ThemeLogic
import com.bselzer.gw2.manager.common.ui.layout.main.model.settings.ThemeResources
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.resource.Resources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch

class SettingsViewModel(context: AppComponentContext) : MainViewModel(context) {
    override val title: StringDesc = Resources.strings.settings.desc()

    val themeResources
        @Composable
        get() = ThemeResources(
            icon = when (LocalTheme.current) {
                Theme.LIGHT -> Gw2Resources.images.gw2_sunrise
                Theme.DARK -> Gw2Resources.images.gw2_twilight
            },
            title = Resources.strings.theme.desc(),
            subtitle = when (LocalTheme.current) {
                Theme.LIGHT -> Resources.strings.light
                Theme.DARK -> Resources.strings.dark
            }.desc()
        )

    val themeLogic
        @Composable
        get() = run {
            val scope = rememberCoroutineScope()
            ThemeLogic(
                checked = LocalTheme.current != Theme.LIGHT,
                onCheckedChange = { checked ->
                    scope.launch {
                        val theme = if (checked) Theme.DARK else Theme.LIGHT
                        preferences.common.theme.set(theme)
                    }
                }
            )
        }
}