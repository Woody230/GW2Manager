package com.bselzer.gw2.manager.common.ui.layout.custom.preference.content

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.TokenViewModel
import com.bselzer.ktx.compose.ui.layout.alertdialog.AlertDialogInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.DialogState
import com.bselzer.ktx.compose.ui.layout.alertdialog.openOnClick
import com.bselzer.ktx.compose.ui.layout.alertdialog.triText
import com.bselzer.ktx.compose.ui.layout.preference.PreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.alertdialog.AlertDialogPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.textfield.TextFieldPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.textfield.TextFieldPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.snackbarhost.LocalSnackbarHostState
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import com.bselzer.ktx.compose.ui.layout.textfield.TextFieldInteractor
import com.bselzer.ktx.compose.ui.text.withHyperlink
import com.bselzer.ktx.resource.images.painter
import com.bselzer.ktx.resource.strings.localized

class TokenComposition(
    model: TokenViewModel,
    private val state: MutableState<DialogState>,
) : ViewModelComposition<TokenViewModel>(model) {
    @Composable
    override fun TokenViewModel.Content(modifier: Modifier) {
        projector().Projection(modifier = state.openOnClick().then(modifier))
    }

    @Composable
    private fun TokenViewModel.projector() = TextFieldPreferenceProjector(
        interactor = interactor()
    )

    @Composable
    private fun TokenViewModel.interactor() = TextFieldPreferenceInteractor(
        preference = AlertDialogPreferenceInteractor(
            preference = preferenceInteractor(),
            dialog = dialogInteractor(),
        ),
        inputDescription = inputDescription(),
        input = input()
    )

    @Composable
    private fun TokenViewModel.preferenceInteractor() = PreferenceInteractor(
        painter = resources.image.painter(),
        title = resources.title.localized(),
        subtitle = resources.subtitle.localized()
    )

    @Composable
    private fun TokenViewModel.dialogInteractor() = AlertDialogInteractor.Builder(state) {
        logic.clearInput()
    }.triText().build {
        title = resources.title.localized()
        closeOnNeutral { logic.onReset() }
        positive(scope = this)
    }

    @Composable
    private fun TokenViewModel.positive(scope: AlertDialogInteractor.Builder) = with(scope) {
        val host = LocalSnackbarHostState.current
        val failure = resources.failure.localized()
        positiveEnabled = resources.dialogInput.localized().isNotBlank()
        closeOnPositive {
            if (!logic.onSave()) {
                host.showSnackbar(message = failure, duration = SnackbarDuration.Long)
            }
        }
    }

    @Composable
    private fun TokenViewModel.inputDescription() = TextInteractor(
        text = inputDescriptionText()
    )

    @Composable
    private fun TokenViewModel.inputDescriptionText() = buildAnnotatedString {
       withHyperlink(
            hypertext = resources.dialogSubtitle.localized(),
            hyperlink = resources.hyperlink.localized()
        )
    }

    @Composable
    private fun TokenViewModel.input() = TextFieldInteractor(
        value = resources.dialogInput.localized(),
        onValueChange = { logic.updateInput(it) }
    )
}