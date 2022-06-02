package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
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
import com.bselzer.ktx.compose.ui.layout.background.image.BackgroundImage
import com.bselzer.ktx.compose.ui.layout.image.ImageInteractor
import com.bselzer.ktx.compose.ui.layout.image.ImagePresenter
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
    override fun SettingsViewModel.Content() = BackgroundImage(
        modifier = Modifier.fillMaxSize(),
        painter = relativeBackgroundPainter,
        presenter = relativeBackgroundPresenter
    ) {
        Preferences()
    }

    @Composable
    private fun SettingsViewModel.Preferences() = preferenceColumnProjector().Projection(
        modifier = Modifier.padding(paddingValues).verticalScroll(rememberScrollState()),
        content = buildArray {
            // TODO language
            add { CommonSection() }

            /* TODO enable token preference when needed
            add { TokenPreference() }
             */

            add { WvwSection() }
        }
    )

    @Composable
    private fun SettingsViewModel.CommonSection() = spacedPreferenceColumnProjector().Projection(
        content = buildArray {
            add { ThemePreference() }
            add { LanguagePreference() }
        }
    )

    @Composable
    private fun SettingsViewModel.ThemePreference() = SwitchPreferenceProjector(
        interactor = SwitchPreferenceInteractor(
            preference = PreferenceInteractor(
                painter = themeResources.image.painter(),
                title = themeResources.title.localized(),
                subtitle = themeResources.subtitle.localized()
            ),
            switch = SwitchInteractor(
                checked = themeLogic.checked,
                onCheckedChange = themeLogic.onCheckedChange
            )
        )
    ).Projection()

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
                dialog = AlertDialogInteractor.Builder(state).triText().build {
                    title = languageResources.title.localized()
                    closeOnPositive { languageLogic.onSave() }
                    closeOnNeutral { languageLogic.onReset() }
                }
            )
        ).Projection(modifier = state.openOnClick()) {
            // TODO desktop only: lazy column inside alert dialog crash https://github.com/JetBrains/compose-jb/issues/1111
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
                    dialog = AlertDialogInteractor.Builder(state).triText().build {
                        title = tokenResources.title.localized()
                        closeOnNeutral { tokenLogic.onReset() }

                        val host = LocalSnackbarHostState.current
                        val failure = tokenResources.failure.localized()
                        closeOnPositive {
                            if (tokenLogic.onSave()) {
                                tokenLogic.clearInput()
                            } else {
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
                    dialog = AlertDialogInteractor.Builder(state).triText().build {
                        title = wvwResources.interval.title.localized()
                        closeOnPositive { intervalLogic.onSave() }
                        closeOnNeutral { intervalLogic.onReset() }
                    }
                )
            ),
        ).Projection(modifier = state.openOnClick())
    }
}
