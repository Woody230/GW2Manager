package com.bselzer.gw2.manager.common.ui.layout.custom.preference.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.RefreshIntervalViewModel
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.resource.ui.layout.alertdialog.triText
import com.bselzer.ktx.compose.resource.ui.layout.icon.downIconInteractor
import com.bselzer.ktx.compose.resource.ui.layout.icon.upIconInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.AlertDialogInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.DialogState
import com.bselzer.ktx.compose.ui.layout.alertdialog.openOnClick
import com.bselzer.ktx.compose.ui.layout.preference.PreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.alertdialog.AlertDialogPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.duration.DurationPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.duration.DurationPreferenceProjector

class RefreshIntervalComposition(
    model: RefreshIntervalViewModel,
    private val state: MutableState<DialogState>
) : ViewModelComposition<RefreshIntervalViewModel>(model) {

    @Composable
    override fun RefreshIntervalViewModel.Content(modifier: Modifier) {
        projector().Projection(state.openOnClick().then(modifier))
    }

    @Composable
    private fun RefreshIntervalViewModel.projector() = DurationPreferenceProjector(
        interactor = interactor()
    )

    @Composable
    private fun RefreshIntervalViewModel.interactor(): DurationPreferenceInteractor {
        val labels = labels
        return DurationPreferenceInteractor(
            amount = logic.amount,
            unit = logic.unit,
            amountRange = logic.amountRange,
            onValueChange = logic.onValueChange,
            units = logic.units,
            unitLabel = { unit -> labels[unit] ?: "" },
            upIcon = upIconInteractor(),
            downIcon = downIconInteractor(),
            preference = AlertDialogPreferenceInteractor(
                preference = preferenceInteractor(),
                dialog = dialogInteractor()
            )
        )
    }

    @Composable
    private fun RefreshIntervalViewModel.preferenceInteractor() = PreferenceInteractor(
        painter = resources.image.painter(),
        title = resources.title.localized(),
        subtitle = resources.subtitle.localized()
    )

    @Composable
    private fun RefreshIntervalViewModel.dialogInteractor() = AlertDialogInteractor.Builder(state) {
        logic.clearInput()
    }.triText().build {
        title = resources.title.localized()
        closeOnPositive { logic.onSave() }
        closeOnNeutral { logic.onReset() }
    }
}