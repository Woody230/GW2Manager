package com.bselzer.gw2.manager.common.ui.layout.main.content.match.overview

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.arkivanov.decompose.router.bringToFront
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.host.content.LocalDialogRouter
import com.bselzer.gw2.manager.common.ui.layout.main.model.match.overview.WorldResources
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.preference.text.TextPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.text.TextPreferencePresenter
import com.bselzer.ktx.compose.ui.layout.preference.text.TextPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.text.TextPresenter

/**
 * Lays out the selected world with the ability to show the dialog for a new selection.
 */
class SelectedWorldComposition(
    model: WorldResources
) : ModelComposition<WorldResources>(model) {
    @Composable
    override fun WorldResources.Content(
        modifier: Modifier
    ) = projector().Projection(modifier.combinedModifier())

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