package com.bselzer.gw2.manager.common.ui.layout.custom.preference.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.StatusViewModel
import com.bselzer.ktx.compose.ui.layout.preference.PreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.switch.SwitchPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.switch.SwitchPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.switch.SwitchInteractor
import com.bselzer.ktx.resource.images.painter
import com.bselzer.ktx.resource.strings.localized

class StatusComposition(
    model: StatusViewModel
): ViewModelComposition<StatusViewModel>(model) {
    @Composable
    override fun StatusViewModel.Content(modifier: Modifier) {
        projector().Projection()
    }

    @Composable
    private fun StatusViewModel.projector() = SwitchPreferenceProjector(
        interactor = SwitchPreferenceInteractor(
            preference = preferenceInteractor(),
            switch = switchInteractor()
        )
    )

    @Composable
    private fun StatusViewModel.preferenceInteractor() = PreferenceInteractor(
        painter = resources.image.painter(),
        title = resources.title.localized(),
        subtitle = resources.subtitle.localized()
    )

    @Composable
    private fun StatusViewModel.switchInteractor() = SwitchInteractor(
        checked = logic.checked,
        onCheckedChange = logic.onCheckedChange
    )
}