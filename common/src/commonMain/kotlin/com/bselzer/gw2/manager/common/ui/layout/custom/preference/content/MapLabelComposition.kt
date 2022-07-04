package com.bselzer.gw2.manager.common.ui.layout.custom.preference.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.MapLabelViewModel
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.preference.PreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.switch.SwitchPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.switch.SwitchPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.switch.SwitchInteractor

class MapLabelComposition(
    model: MapLabelViewModel
) : ViewModelComposition<MapLabelViewModel>(model) {
    @Composable
    override fun MapLabelViewModel.Content(modifier: Modifier) {
        projector().Projection()
    }

    @Composable
    private fun MapLabelViewModel.projector() = SwitchPreferenceProjector(
        interactor = SwitchPreferenceInteractor(
            preference = preferenceInteractor(),
            switch = switchInteractor()
        )
    )

    @Composable
    private fun MapLabelViewModel.preferenceInteractor() = PreferenceInteractor(
        painter = resources.image.painter(),
        title = resources.title.localized(),
        subtitle = resources.subtitle.localized()
    )

    @Composable
    private fun MapLabelViewModel.switchInteractor() = SwitchInteractor(
        checked = logic.checked,
        onCheckedChange = logic.onCheckedChange
    )
}