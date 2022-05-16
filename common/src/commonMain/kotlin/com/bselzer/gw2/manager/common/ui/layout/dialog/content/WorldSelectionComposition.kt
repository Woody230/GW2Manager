package com.bselzer.gw2.manager.common.ui.layout.dialog.content

import androidx.compose.runtime.Composable
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.WorldSelectionViewModel
import com.bselzer.ktx.compose.resource.ui.layout.alertdialog.resetAlertDialogInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.AlertDialogProjector
import com.bselzer.ktx.compose.ui.layout.alertdialog.singlechoice.SingleChoiceInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.singlechoice.SingleChoiceProjector
import com.bselzer.ktx.compose.ui.notification.snackbar.ShowSnackbar
import com.bselzer.ktx.logging.Logger
import dev.icerock.moko.resources.compose.localized

class WorldSelectionComposition(
    private val onFinish: () -> Unit
) : ViewModelComposition<WorldSelectionViewModel>() {
    @Composable
    override fun Content(model: WorldSelectionViewModel): Unit = model.run {
        if (noWorlds.enabled) {
            ShowSnackbar(message = noWorlds.message.localized())
        } else {
            SelectionDialog()
        }
    }

    @Composable
    private fun WorldSelectionViewModel.SelectionDialog() = AlertDialogProjector(
        interactor = resetAlertDialogInteractor { onFinish() }
    ).Projection {
        SelectionChoice()
    }

    // TODO preferably, choice should be scrolled to when dialog gets opened
    // TODO if current selected does not exist, do not allow cancellation
    @Composable
    private fun WorldSelectionViewModel.SelectionChoice() = SingleChoiceProjector(
        interactor = SingleChoiceInteractor(
            selected = selection.selected,
            values = selection.values,
            getLabel = selection.getLabel,
            onSelection = { world ->
                Logger.d("Setting world to $world")
                save(world.id)
            }
        )
    )
}