package com.bselzer.gw2.manager.common.ui.layout.custom.preference.content

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.ColorViewModel
import com.bselzer.gw2.manager.common.ui.theme.ThemedColorFilter
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.resource.ui.layout.alertdialog.triText
import com.bselzer.ktx.compose.resource.ui.layout.text.textInteractor
import com.bselzer.ktx.compose.ui.graphics.color.colorOrNull
import com.bselzer.ktx.compose.ui.layout.alertdialog.AlertDialogInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.DialogState
import com.bselzer.ktx.compose.ui.layout.alertdialog.openOnClick
import com.bselzer.ktx.compose.ui.layout.image.ImagePresenter
import com.bselzer.ktx.compose.ui.layout.preference.PreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.PreferencePresenter
import com.bselzer.ktx.compose.ui.layout.preference.alertdialog.AlertDialogPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.alertdialog.AlertDialogPreferencePresenter
import com.bselzer.ktx.compose.ui.layout.preference.textfield.TextFieldPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.textfield.TextFieldPreferencePresenter
import com.bselzer.ktx.compose.ui.layout.preference.textfield.TextFieldPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.textfield.TextFieldInteractor
import com.bselzer.ktx.compose.ui.notification.snackbar.LocalSnackbarHostState

class ColorComposition(
    model: ColorViewModel,
    private val state: MutableState<DialogState>
) : ModelComposition<ColorViewModel>(model) {
    @Composable
    override fun ColorViewModel.Content(modifier: Modifier) {
        projector().Projection(modifier = state.openOnClick().then(modifier))
    }

    @Composable
    private fun ColorViewModel.projector() = TextFieldPreferenceProjector(
        presenter = presenter(),
        interactor = interactor()
    )

    @Composable
    private fun ColorViewModel.interactor() = TextFieldPreferenceInteractor(
        preference = AlertDialogPreferenceInteractor(
            preference = preferenceInteractor(),
            dialog = dialogInteractor()
        ),
        inputDescription = resources.dialogSubtitle.textInteractor(),
        input = input()
    )

    @Composable
    private fun ColorViewModel.presenter(): TextFieldPreferencePresenter {
        val selected = resources.subtitle.colorOrNull()
        val colorFilter = if (selected == null) ThemedColorFilter else ColorFilter.tint(color = selected)
        return TextFieldPreferencePresenter(
            preference = AlertDialogPreferencePresenter(
                preference = PreferencePresenter(
                    // Color image is by default harder to see in dark mode.
                    image = ImagePresenter(colorFilter = colorFilter)
                )
            ),
        )
    }

    @Composable
    private fun ColorViewModel.preferenceInteractor() = PreferenceInteractor(
        painter = resources.image.painter(),
        title = resources.title.localized(),
        subtitle = resources.subtitle.value
    )

    @Composable
    private fun ColorViewModel.dialogInteractor() = AlertDialogInteractor.Builder(state) {
        logic.clearInput()
    }.triText().build {
        title = resources.title.localized()
        closeOnNeutral { logic.onReset() }
        positive(scope = this)
    }

    @Composable
    private fun ColorViewModel.positive(scope: AlertDialogInteractor.Builder) = with(scope) {
        val host = LocalSnackbarHostState.current
        val failure = resources.failure.localized()
        positiveEnabled = resources.hasValidInput
        closeOnPositive {
            if (!logic.onSave()) {
                host.showSnackbar(message = failure, duration = SnackbarDuration.Long)
            }
        }
    }

    @Composable
    private fun ColorViewModel.input() = TextFieldInteractor(
        value = resources.dialogInput.localized(),
        onValueChange = { logic.updateInput(it) }
    )
}