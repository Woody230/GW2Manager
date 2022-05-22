package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.SettingsViewModel
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.resource.ui.layout.alertdialog.resetAlertDialogInteractor
import com.bselzer.ktx.compose.resource.ui.layout.icon.downIconInteractor
import com.bselzer.ktx.compose.resource.ui.layout.icon.upIconInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.DialogState
import com.bselzer.ktx.compose.ui.layout.background.image.BackgroundImage
import com.bselzer.ktx.compose.ui.layout.description.DescriptionInteractor
import com.bselzer.ktx.compose.ui.layout.image.ImageInteractor
import com.bselzer.ktx.compose.ui.layout.preference.PreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.alertdialog.AlertDialogPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.duration.DurationPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.duration.DurationPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.preference.section.PreferenceSectionInteractor
import com.bselzer.ktx.compose.ui.layout.preference.section.PreferenceSectionPresenter
import com.bselzer.ktx.compose.ui.layout.preference.section.PreferenceSectionProjector
import com.bselzer.ktx.compose.ui.layout.preference.section.preferenceColumnProjector
import com.bselzer.ktx.compose.ui.layout.preference.switch.SwitchPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.switch.SwitchPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.preference.textfield.TextFieldPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.textfield.TextFieldPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.switch.SwitchInteractor
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import com.bselzer.ktx.compose.ui.layout.text.TextPresenter
import com.bselzer.ktx.compose.ui.layout.text.hyperlink
import com.bselzer.ktx.compose.ui.layout.textfield.TextFieldInteractor
import com.bselzer.ktx.compose.ui.notification.snackbar.LocalSnackbarHostState
import com.bselzer.ktx.function.collection.buildArray
import dev.icerock.moko.resources.compose.localized
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit

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
        modifier = Modifier.padding(paddingValues).verticalScroll(rememberScrollState()),
        content = buildArray {
            // TODO language
            add { ThemePreference() }

            /* TODO enable token preference when needed
            add { TokenPreference() }
             */

            add { WvwSection() }
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
                                    tokenLogic.clearInput()
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
                    value = tokenResources.dialogInput.localized(),
                    onValueChange = { tokenLogic.updateInput(it) }
                )
            )
        ).Projection(modifier = Modifier.clickable {
            state = DialogState.OPENED
        })
    }

    @Composable
    private fun SettingsViewModel.WvwSection() = PreferenceSectionProjector(
        interactor = PreferenceSectionInteractor(
            image = ImageInteractor(
                painter = wvwResources.image.painter(),
                contentDescription = wvwResources.title.localized()
            ),
            title = TextInteractor(text = wvwResources.title.localized())
        ),
        presenter = PreferenceSectionPresenter(
            title = TextPresenter(color = MaterialTheme.colors.primary)
        )
    ).Projection {
        RefreshInterval()
    }

    @Composable
    private fun SettingsViewModel.RefreshInterval() {
        val scope = rememberCoroutineScope()
        var state by remember { mutableStateOf(DialogState.CLOSED) }
        val labels: Map<DurationUnit, String> = intervalLogic.units.associateWith { unit -> wvwResources.interval.label(unit).localized() }
        DurationPreferenceProjector(
            interactor = DurationPreferenceInteractor(
                amount = intervalLogic.amount,
                unit = intervalLogic.unit,
                amountRange = intervalLogic.amountRange,
                onValueChange = intervalLogic.onValueChange,
                units = intervalLogic.units,
                unitLabel = { unit -> labels[unit] ?: "" },
                upIcon = upIconInteractor(),
                downIcon = downIconInteractor(),
                preference = AlertDialogPreferenceInteractor(
                    preference = PreferenceInteractor(
                        image = ImageInteractor(
                            painter = wvwResources.interval.image.painter(),
                            contentDescription = wvwResources.interval.title.localized(),
                        ),
                        description = DescriptionInteractor(
                            title = TextInteractor(text = wvwResources.interval.title.localized()),
                            subtitle = TextInteractor(text = wvwResources.interval.subtitle.localized())
                        )
                    ),
                    dialog = resetAlertDialogInteractor(
                        onConfirmation = {
                            scope.launch { intervalLogic.onSave() }
                            DialogState.CLOSED
                        },
                        onReset = {
                            scope.launch { intervalLogic.onReset() }
                            DialogState.CLOSED
                        },
                        closeDialog = {
                            state = DialogState.CLOSED
                        }
                    ).copy(
                        state = state,
                        title = TextInteractor(
                            text = wvwResources.interval.title.localized()
                        ),
                    )
                )
            ),
        ).Projection(modifier = Modifier.clickable {
            state = DialogState.OPENED
        })
    }
}
