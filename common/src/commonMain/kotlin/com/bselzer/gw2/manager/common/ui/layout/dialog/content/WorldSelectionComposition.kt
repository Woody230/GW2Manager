package com.bselzer.gw2.manager.common.ui.layout.dialog.content

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.activeChild
import com.arkivanov.decompose.router.bringToFront
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.WorldSelectionViewModel
import com.bselzer.gw2.manager.common.ui.layout.host.content.LocalDialogRouter
import com.bselzer.ktx.compose.resource.ui.layout.alertdialog.resetAlertDialogInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.AlertDialogProjector
import com.bselzer.ktx.compose.ui.layout.alertdialog.singlechoice.SingleChoiceInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.singlechoice.SingleChoiceProjector
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import com.bselzer.ktx.compose.ui.notification.snackbar.LocalSnackbarHostState
import com.bselzer.ktx.logging.Logger
import dev.icerock.moko.resources.compose.localized

class WorldSelectionComposition : ViewModelComposition<WorldSelectionViewModel>() {
    @Composable
    override fun Content(model: WorldSelectionViewModel): Unit = model.run {
        if (noWorlds.enabled) {
            NoWorldsMessage()
        } else {
            SelectionDialog()
        }
    }

    @Composable
    private fun WorldSelectionViewModel.NoWorldsMessage() {
        val host = LocalSnackbarHostState.current
        val message = noWorlds.message.localized()
        val dialogRouter = LocalDialogRouter.current

        // Only display the message once per dialog initialization.
        LaunchedEffect(dialogRouter.activeChild) {
            host.showSnackbar(message = message)
            dialogRouter.bringToFront(DialogConfig.NoDialogConfig)
        }
    }


    @Composable
    private fun WorldSelectionViewModel.SelectionDialog() {
        val dialogRouter = LocalDialogRouter.current
        AlertDialogProjector(
            interactor = resetAlertDialogInteractor {
                // Don't show the dialog anymore when the world has been selected.
                dialogRouter.bringToFront(DialogConfig.NoDialogConfig)
            }.copy(
                title = TextInteractor(selection.title.localized())
            )
        ).Projection(
            // TODO use regular dialog instead of constrained -- title bounces with the choices
            constrained = true
        ) {
            SelectionChoice()
        }
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
    ).Projection(modifier = Modifier.fillMaxHeight())
}