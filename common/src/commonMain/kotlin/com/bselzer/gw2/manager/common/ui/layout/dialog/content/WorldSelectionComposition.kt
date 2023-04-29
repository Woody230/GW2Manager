package com.bselzer.gw2.manager.common.ui.layout.dialog.content

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.stack.bringToFront
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.WorldSelectionViewModel
import com.bselzer.gw2.manager.common.ui.layout.host.content.LocalDialogRouter
import com.bselzer.ktx.compose.ui.layout.alertdialog.AlertDialogInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.AlertDialogProjector
import com.bselzer.ktx.compose.ui.layout.alertdialog.biText
import com.bselzer.ktx.compose.ui.layout.alertdialog.singlechoice.SingleChoiceInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.singlechoice.SingleChoiceProjector
import com.bselzer.ktx.compose.ui.layout.snackbarhost.LocalSnackbarHostState
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.resource.strings.localized

class WorldSelectionComposition(
    model: WorldSelectionViewModel
) : ViewModelComposition<WorldSelectionViewModel>(model) {
    @Composable
    override fun WorldSelectionViewModel.Content(modifier: Modifier) {
        if (noWorlds.enabled) {
            NoWorldsMessage()
        } else {
            SelectionDialog(modifier)
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
    private fun WorldSelectionViewModel.SelectionDialog(
        modifier: Modifier
    ) = AlertDialogProjector(
        interactor = interactor()
    ).Projection(
        modifier = modifier,

        // TODO use regular dialog instead of constrained -- title bounces with the choices
        constrained = true
    ) {
        SelectionChoice()
    }

    @Composable
    private fun WorldSelectionViewModel.interactor() = interactorBuilder().biText().build {
        title = selection.title.localized()
        neutral()
        positive()
    }

    @Composable
    private fun WorldSelectionViewModel.interactorBuilder() = run {
        val dialogRouter = LocalDialogRouter.current
        AlertDialogInteractor.Builder {
            // Don't show the dialog anymore when the world has been selected.
            dialogRouter.bringToFront(DialogConfig.NoDialogConfig)

            // Reset the choice so that it does not persist when the dialog is reopened.
            selection.resetSelected()
        }
    }

    @Composable
    private fun AlertDialogInteractor.Builder.neutral() = with(model) {
        closeOnNeutral {
            // NOTE Not using triText so currently resetting is disabled.
            selection.onReset()
        }
    }

    @Composable
    private fun AlertDialogInteractor.Builder.positive() = with(model) {
        positiveEnabled = selection.selected != null
        closeOnPositive {
            // TODO keep open if world not selected and force show dialog on launch if no selection?
            val world = selection.selected
            if (world != null) {
                Logger.d("Setting world to $world")
                selection.onSave(world)
            }
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
            onSelection = { world -> selection.setSelected(world) }
        )
    ).Projection(modifier = Modifier.fillMaxHeight())
}