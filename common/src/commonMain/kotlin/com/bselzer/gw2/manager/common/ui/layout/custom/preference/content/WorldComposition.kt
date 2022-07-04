package com.bselzer.gw2.manager.common.ui.layout.custom.preference.content

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.arkivanov.decompose.router.bringToFront
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.WorldResources
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.WorldViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.host.content.LocalDialogRouter
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.preference.text.TextPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.text.TextPreferencePresenter
import com.bselzer.ktx.compose.ui.layout.preference.text.TextPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.text.TextPresenter

class WorldComposition(
    model: WorldViewModel
) : ViewModelComposition<WorldViewModel>(model) {
    @Composable
    override fun WorldViewModel.Content(modifier: Modifier) {
        worldResources.projector().Projection(modifier.combinedModifier())
    }

    @Composable
    private fun WorldResources.projector() = TextPreferenceProjector(
        interactor = interactor(),
        presenter = presenter()
    )

    @Composable
    private fun WorldResources.interactor() = TextPreferenceInteractor(
        painter = image.painter(),
        title = title.localized(),
        subtitle = subtitle.localized(),
    )

    @Composable
    private fun WorldResources.presenter() = TextPreferencePresenter(
        subtitle = TextPresenter(color = color, fontWeight = FontWeight.Bold)
    )

    @Composable
    private fun Modifier.combinedModifier(): Modifier {
        val dialogRouter = LocalDialogRouter.current
        return clickable {
            // Open up the world selection dialog so that the user can pick another world.
            dialogRouter.bringToFront(DialogConfig.WorldSelectionConfig)
        }
    }
}