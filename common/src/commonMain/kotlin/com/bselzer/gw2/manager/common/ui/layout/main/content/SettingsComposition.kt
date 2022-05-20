package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.SettingsViewModel
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.resource.ui.layout.alertdialog.resetAlertDialogInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.DialogState
import com.bselzer.ktx.compose.ui.layout.background.image.BackgroundImage
import com.bselzer.ktx.compose.ui.layout.description.DescriptionInteractor
import com.bselzer.ktx.compose.ui.layout.image.ImageInteractor
import com.bselzer.ktx.compose.ui.layout.preference.PreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.alertdialog.AlertDialogPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.section.preferenceColumnProjector
import com.bselzer.ktx.compose.ui.layout.preference.switch.SwitchPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.switch.SwitchPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.preference.textfield.TextFieldPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.textfield.TextFieldPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.switch.SwitchInteractor
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import com.bselzer.ktx.compose.ui.layout.text.hyperlink
import com.bselzer.ktx.compose.ui.layout.textfield.TextFieldInteractor
import com.bselzer.ktx.compose.ui.notification.snackbar.LocalSnackbarHostState
import com.bselzer.ktx.function.collection.buildArray
import dev.icerock.moko.resources.compose.localized
import kotlinx.coroutines.launch

class SettingsComposition : ViewModelComposition<SettingsViewModel>() {
    @Composable
    override fun Content(model: SettingsViewModel) = model.run {
        BackgroundImage(
            modifier = Modifier.fillMaxSize(),
            painter = relativeBackgroundPainter
        ) {
            Preferences()
        }
    }

    @Composable
    private fun SettingsViewModel.Preferences() = preferenceColumnProjector().Projection(
        modifier = Modifier.padding(25.dp),
        content = buildArray {
            // TODO language
            add { ThemePreference() }
            add { TokenPreference() }
        }
    )

    @Composable
    private fun SettingsViewModel.ThemePreference() = SwitchPreferenceProjector(
        interactor = SwitchPreferenceInteractor(
            preference = PreferenceInteractor(
                image = ImageInteractor(
                    painter = themeResources.image.painter(),
                    contentDescription = themeResources.subtitle.localized()
                ),
                description = DescriptionInteractor(
                    title = TextInteractor(text = themeResources.title.localized()),
                    subtitle = TextInteractor(text = themeResources.subtitle.localized())
                )
            ),
            switch = SwitchInteractor(
                checked = themeLogic.checked,
                onCheckedChange = themeLogic.onCheckedChange
            )
        )
    ).Projection()

    @Composable
    private fun SettingsViewModel.TokenPreference() {
        val scope = rememberCoroutineScope()
        val host = LocalSnackbarHostState.current
        val tag = "applications"
        var state by remember { mutableStateOf(DialogState.CLOSED) }
        val failure = tokenResources.failure.localized()
        TextFieldPreferenceProjector(
            interactor = TextFieldPreferenceInteractor(
                preference = AlertDialogPreferenceInteractor(
                    preference = PreferenceInteractor(
                        image = ImageInteractor(
                            painter = tokenResources.image.painter(),
                            contentDescription = tokenResources.title.localized(),
                        ),
                        description = DescriptionInteractor(
                            title = TextInteractor(text = tokenResources.title.localized()),
                            subtitle = TextInteractor(text = tokenResources.subtitle.localized())
                        )
                    ),
                    dialog = resetAlertDialogInteractor(
                        onConfirmation = {
                            scope.launch {
                                if (tokenLogic.onSave()) {
                                    tokenLogic.clearToken()
                                } else {
                                    host.showSnackbar(message = failure, duration = SnackbarDuration.Long)
                                }
                            }

                            DialogState.CLOSED
                        },
                        onReset = {
                            scope.launch { tokenLogic.onReset() }
                            DialogState.CLOSED
                        },
                        closeDialog = {
                            state = DialogState.CLOSED
                        }
                    ).copy(
                        state = state,
                        title = TextInteractor(
                            text = tokenResources.title.localized()
                        ),
                    )
                ),
                inputDescription = TextInteractor(
                    text = buildAnnotatedString {
                        hyperlink(
                            text = tokenResources.dialogSubtitle.localized(),
                            tag = tag,
                            hyperlink = tokenResources.hyperlink.localized()
                        )
                    },
                    onClickOffset = { offset, text ->
                        text.getStringAnnotations(tag = tag, start = offset, end = offset).firstOrNull()?.let {
                            // Open the link in the user's browser.
                            tokenLogic.onClickHyperlink(it.item)
                        }
                    },
                ),
                input = TextFieldInteractor(
                    value = tokenLogic.token() ?: "",
                    onValueChange = { tokenLogic.updateToken(it) }
                )
            )
        ).Projection(modifier = Modifier.clickable {
            state = DialogState.OPENED
        })
    }
}
