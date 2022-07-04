package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.layout.common.RelativeBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.content.*
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.SettingsViewModel
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.alertdialog.rememberDialogState
import com.bselzer.ktx.compose.ui.layout.preference.section.*
import com.bselzer.ktx.compose.ui.layout.text.TextPresenter
import com.bselzer.ktx.function.collection.buildArray

class SettingsComposition(model: SettingsViewModel) : MainChildComposition<SettingsViewModel>(model) {
    @Composable
    override fun SettingsViewModel.Content(modifier: Modifier) = RelativeBackgroundImage(
        modifier = Modifier.fillMaxSize().then(modifier),
    ) {
        Preferences()
    }

    @Composable
    private fun SettingsViewModel.Preferences() = preferenceColumnProjector().Projection(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(paddingValues),
        content = buildArray {
            add { CommonSection() }
            add { WvwSection() }
        }
    )

    @Composable
    private fun SettingsViewModel.CommonSection() = spacedPreferenceColumnProjector().Projection(
        content = buildArray {
            add { ThemeComposition(theme).Content() }
            add { LanguageComposition(language, rememberDialogState()).Content() }

            // TODO enable token preference when needed
            if (false) {
                add { TokenComposition(token, rememberDialogState()).Content() }
            }
        }
    )

    @Composable
    private fun SettingsViewModel.WvwSection() = PreferenceSectionProjector(
        interactor = wvwSectionInteractor(),
        presenter = wvwSectionPresenter()
    ).Projection {
        WvwSectionContent()
    }

    @Composable
    private fun SettingsViewModel.wvwSectionInteractor() = PreferenceSectionInteractor(
        title = wvwResources.title.localized(),
        painter = wvwResources.image.painter(),
    )

    @Composable
    private fun wvwSectionPresenter() = PreferenceSectionPresenter(
        title = TextPresenter(color = MaterialTheme.colors.primary)
    )

    @Composable
    private fun SettingsViewModel.WvwSectionContent() = spacedPreferenceColumnProjector().Projection(
        content = buildArray {
            add { RefreshIntervalComposition(refreshInterval, rememberDialogState()).Content() }
            add { ZoomComposition(zoom, rememberDialogState()).Content() }
            add { MapLabelComposition(mapLabel).Content() }

            colors.forEach { color ->
                add { ColorComposition(color, rememberDialogState()).Content() }
            }
        }
    )
}
