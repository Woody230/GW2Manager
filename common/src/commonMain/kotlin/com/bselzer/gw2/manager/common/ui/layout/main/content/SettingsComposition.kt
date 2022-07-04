package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.layout.common.RelativeBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.content.ColorComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.content.LanguageComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.content.ThemeComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.content.TokenComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.SettingsViewModel
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.resource.ui.layout.alertdialog.triText
import com.bselzer.ktx.compose.resource.ui.layout.icon.downIconInteractor
import com.bselzer.ktx.compose.resource.ui.layout.icon.upIconInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.AlertDialogInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.openOnClick
import com.bselzer.ktx.compose.ui.layout.alertdialog.rememberDialogState
import com.bselzer.ktx.compose.ui.layout.picker.IntegerPickerInteractor
import com.bselzer.ktx.compose.ui.layout.picker.PickerProjector
import com.bselzer.ktx.compose.ui.layout.preference.PreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.alertdialog.AlertDialogPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.alertdialog.AlertDialogPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.preference.duration.DurationPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.duration.DurationPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.preference.section.*
import com.bselzer.ktx.compose.ui.layout.preference.switch.SwitchPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.switch.SwitchPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.switch.SwitchInteractor
import com.bselzer.ktx.compose.ui.layout.text.TextPresenter
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
            add { LanguageComposition(language, rememberDialogState()).Content() }

            // TODO enable token preference when needed
            if (false) {
                add { TokenComposition(token, rememberDialogState()).Content() }
            }
        }
    )

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
                    add { ColorComposition(color, rememberDialogState()).Content() }
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
