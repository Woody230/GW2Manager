package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import com.bselzer.gw2.manager.common.ui.layout.common.RelativeBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.content.ColorComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.content.ThemeComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.SettingsViewModel
import com.bselzer.gw2.manager.common.ui.theme.ThemedColorFilter
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.resource.ui.layout.alertdialog.triText
import com.bselzer.ktx.compose.resource.ui.layout.icon.downIconInteractor
import com.bselzer.ktx.compose.resource.ui.layout.icon.upIconInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.AlertDialogInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.openOnClick
import com.bselzer.ktx.compose.ui.layout.alertdialog.rememberDialogState
import com.bselzer.ktx.compose.ui.layout.alertdialog.singlechoice.SingleChoiceInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.singlechoice.SingleChoiceProjector
import com.bselzer.ktx.compose.ui.layout.image.ImagePresenter
import com.bselzer.ktx.compose.ui.layout.picker.IntegerPickerInteractor
import com.bselzer.ktx.compose.ui.layout.picker.PickerProjector
import com.bselzer.ktx.compose.ui.layout.preference.PreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.PreferencePresenter
import com.bselzer.ktx.compose.ui.layout.preference.alertdialog.AlertDialogPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.alertdialog.AlertDialogPreferencePresenter
import com.bselzer.ktx.compose.ui.layout.preference.alertdialog.AlertDialogPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.preference.duration.DurationPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.duration.DurationPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.preference.section.*
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
import kotlin.time.DurationUnit

class SettingsComposition(model: SettingsViewModel) : MainChildComposition<SettingsViewModel>(model) {
    @Composable
    override fun SettingsViewModel.Content(modifier: Modifier) = RelativeBackgroundImage(
        modifier = Modifier.fillMaxSize().then(modifier),
    ) {
        Preferences()
    }

    @Composable
    private fun SettingsViewModel.Preferences() = preferenceColumnProjector().Projection(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(paddingValues),
        content = buildArray {
            add { CommonSection() }
            add { WvwSection() }
        }
    )

    @Composable
    private fun SettingsViewModel.CommonSection() = spacedPreferenceColumnProjector().Projection(
        content = buildArray {
            add { ThemeComposition(theme).Content() }
            add { LanguagePreference() }

            /* TODO enable token preference when needed
            add { TokenPreference() }
             */
        }
    )

    @Composable
    private fun SettingsViewModel.LanguagePreference() {
        val state = rememberDialogState()
        val labels = languageLogic.values.associateWith { locale -> languageResources.getLabel(locale).localized() }
        AlertDialogPreferenceProjector(
            presenter = AlertDialogPreferencePresenter(
                preference = PreferencePresenter(
                    // Language image is by default harder to see in dark mode.
                    image = ImagePresenter(colorFilter = ThemedColorFilter)
                )
            ),
            interactor = AlertDialogPreferenceInteractor(
                preference = PreferenceInteractor(
                    painter = languageResources.image.painter(),
                    title = languageResources.title.localized(),
                    subtitle = languageResources.subtitle.localized()
                ),
                dialog = AlertDialogInteractor.Builder(state) {
                    languageLogic.resetSelection()
                }.triText().build {
                    title = languageResources.title.localized()
                    closeOnPositive { languageLogic.onSave() }
                    closeOnNeutral { languageLogic.onReset() }
                }
            )
        ).Projection(modifier = state.openOnClick()) {
            // TODO Desktop: lazy column inside alert dialog crash https://github.com/JetBrains/compose-jb/issues/1111
            SingleChoiceProjector(
                interactor = SingleChoiceInteractor(
                    selected = languageLogic.selected(),
                    values = languageLogic.values.sortedBy { locale -> labels[locale] },
                    getLabel = { locale -> labels[locale] ?: "" },
                    onSelection = { locale -> languageLogic.updateSelection(locale) }
                )
            ).Projection()
        }
    }

    @Composable
    private fun SettingsViewModel.TokenPreference() {
        val tag = "applications"
        val state = rememberDialogState()
        TextFieldPreferenceProjector(
            interactor = TextFieldPreferenceInteractor(
                preference = AlertDialogPreferenceInteractor(
                    preference = PreferenceInteractor(
                        painter = tokenResources.image.painter(),
                        title = tokenResources.title.localized(),
                        subtitle = tokenResources.subtitle.localized()
                    ),
                    dialog = AlertDialogInteractor.Builder(state) {
                        tokenLogic.clearInput()
                    }.triText().build {
                        title = tokenResources.title.localized()
                        closeOnNeutral { tokenLogic.onReset() }

                        val host = LocalSnackbarHostState.current
                        val failure = tokenResources.failure.localized()
                        positiveEnabled = tokenResources.dialogInput.localized().isNotBlank()
                        closeOnPositive {
                            if (!tokenLogic.onSave()) {
                                host.showSnackbar(message = failure, duration = SnackbarDuration.Long)
                            }
                        }
                    }
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
        ).Projection(modifier = state.openOnClick())
    }

    @Composable
    private fun SettingsViewModel.WvwSection() = PreferenceSectionProjector(
        interactor = PreferenceSectionInteractor(
            title = wvwResources.title.localized(),
            painter = wvwResources.image.painter(),
        ),
        presenter = PreferenceSectionPresenter(
            title = TextPresenter(color = MaterialTheme.colors.primary)
        )
    ).Projection {
        spacedPreferenceColumnProjector().Projection(
            content = buildArray {
                add { RefreshInterval() }
                add { Zoom() }
                add { MapLabel() }

                colors.forEach { color ->
                    add { ColorComposition(color).Content() }
                }
            }
        )
    }

    @Composable
    private fun SettingsViewModel.RefreshInterval() {
        val state = rememberDialogState()
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
                        painter = wvwResources.interval.image.painter(),
                        title = wvwResources.interval.title.localized(),
                        subtitle = wvwResources.interval.subtitle.localized()
                    ),
                    dialog = AlertDialogInteractor.Builder(state) {
                        intervalLogic.clearInput()
                    }.triText().build {
                        title = wvwResources.interval.title.localized()
                        closeOnPositive { intervalLogic.onSave() }
                        closeOnNeutral { intervalLogic.onReset() }
                    }
                )
            ),
        ).Projection(modifier = state.openOnClick())
    }

    @Composable
    private fun SettingsViewModel.Zoom() {
        val state = rememberDialogState()
        AlertDialogPreferenceProjector(
            interactor = AlertDialogPreferenceInteractor(
                preference = PreferenceInteractor(
                    painter = zoomResources.image.painter(),
                    title = zoomResources.title.localized(),
                    subtitle = zoomResources.subtitle.localized()
                ),
                dialog = AlertDialogInteractor.Builder(state) {
                    zoomLogic.clearInput()
                }.triText().build {
                    title = zoomResources.title.localized()
                    closeOnPositive { zoomLogic.onSave() }
                    closeOnNeutral { zoomLogic.onReset() }
                }
            )
        ).Projection(
            modifier = state.openOnClick()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                PickerProjector(
                    interactor = IntegerPickerInteractor(
                        selected = zoomLogic.amount,
                        range = zoomLogic.amountRange,
                        onSelectionChanged = zoomLogic.onValueChange,
                        upIcon = upIconInteractor(),
                        downIcon = downIconInteractor()
                    )
                ).Projection()
            }
        }
    }

    @Composable
    private fun SettingsViewModel.MapLabel() = SwitchPreferenceProjector(
        interactor = SwitchPreferenceInteractor(
            preference = PreferenceInteractor(
                painter = mapLabelResources.image.painter(),
                title = mapLabelResources.title.localized(),
                subtitle = mapLabelResources.subtitle.localized()
            ),
            switch = SwitchInteractor(
                checked = mapLabelLogic.checked,
                onCheckedChange = mapLabelLogic.onCheckedChange
            )
        )
    ).Projection()
}
