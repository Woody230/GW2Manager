package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.SettingsViewModel
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.ui.layout.background.image.BackgroundImage
import com.bselzer.ktx.compose.ui.layout.description.DescriptionInteractor
import com.bselzer.ktx.compose.ui.layout.image.ImageInteractor
import com.bselzer.ktx.compose.ui.layout.preference.PreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.section.preferenceColumnProjector
import com.bselzer.ktx.compose.ui.layout.preference.switch.SwitchPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.switch.SwitchPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.switch.SwitchInteractor
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import com.bselzer.ktx.function.collection.buildArray
import dev.icerock.moko.resources.compose.localized

class SettingsComposition : ViewModelComposition<SettingsViewModel>() {
    @Composable
    override fun Content(model: SettingsViewModel) = model.run {
        BackgroundImage(
            modifier = Modifier.fillMaxSize(),
            painter = relativeBackgroundPainter
        ) {
            Preferences()
        }
    }

    @Composable
    private fun SettingsViewModel.Preferences() = preferenceColumnProjector().Projection(
        modifier = Modifier.padding(25.dp),
        content = buildArray {
            add { ThemePreference() }
        }
    )

    @Composable
    private fun SettingsViewModel.ThemePreference() = SwitchPreferenceProjector(
        interactor = SwitchPreferenceInteractor(
            preference = PreferenceInteractor(
                image = ImageInteractor(
                    painter = themeResources.icon.painter(),
                    contentDescription = themeResources.subtitle.localized()
                ),
                description = DescriptionInteractor(
                    title = TextInteractor(text = themeResources.title.localized()),
                    subtitle = TextInteractor(text = themeResources.subtitle.localized())
                )
            ),
            switch = SwitchInteractor(
                checked = themeLogic.checked,
                onCheckedChange = themeLogic.onCheckedChange
            )
        )
    ).Projection()
}