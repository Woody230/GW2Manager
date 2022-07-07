package com.bselzer.gw2.manager.common.ui.layout.custom.preference.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.ThemeViewModel
import com.bselzer.ktx.compose.ui.layout.preference.PreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.switch.SwitchPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.switch.SwitchPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.switch.SwitchInteractor
import com.bselzer.ktx.resource.images.painter
import com.bselzer.ktx.resource.strings.localized

class ThemeComposition(
    model: ThemeViewModel
) : ViewModelComposition<ThemeViewModel>(model) {
    @Composable
    override fun ThemeViewModel.Content(modifier: Modifier) = projector().Projection(
        modifier = modifier
    )

    @Composable
    private fun ThemeViewModel.projector() = SwitchPreferenceProjector(
        interactor = interactor()
    )

    @Composable
    private fun ThemeViewModel.interactor() = SwitchPreferenceInteractor(
        preference = preferenceInteractor(),
        switch = switchInteractor()
    )

    @Composable
    private fun ThemeViewModel.preferenceInteractor() = PreferenceInteractor(
        painter = resources.image.painter(),
        title = resources.title.localized(),
        subtitle = resources.subtitle.localized()
    )

    @Composable
    private fun ThemeViewModel.switchInteractor() = SwitchInteractor(
        checked = logic.checked,
        onCheckedChange = logic.onCheckedChange
    )
}