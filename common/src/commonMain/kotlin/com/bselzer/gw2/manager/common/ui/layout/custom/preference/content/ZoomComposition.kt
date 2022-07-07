package com.bselzer.gw2.manager.common.ui.layout.custom.preference.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.ZoomViewModel
import com.bselzer.ktx.compose.ui.layout.alertdialog.AlertDialogInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.DialogState
import com.bselzer.ktx.compose.ui.layout.alertdialog.openOnClick
import com.bselzer.ktx.compose.ui.layout.alertdialog.triText
import com.bselzer.ktx.compose.ui.layout.icon.downIconInteractor
import com.bselzer.ktx.compose.ui.layout.icon.upIconInteractor
import com.bselzer.ktx.compose.ui.layout.picker.IntegerPickerInteractor
import com.bselzer.ktx.compose.ui.layout.picker.PickerProjector
import com.bselzer.ktx.compose.ui.layout.preference.PreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.alertdialog.AlertDialogPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.alertdialog.AlertDialogPreferenceProjector
import com.bselzer.ktx.resource.images.painter
import com.bselzer.ktx.resource.strings.localized

class ZoomComposition(
    model: ZoomViewModel,
    private val state: MutableState<DialogState>
) : ViewModelComposition<ZoomViewModel>(model) {
    @Composable
    override fun ZoomViewModel.Content(
        modifier: Modifier
    ) = projector().Projection(
        modifier = state.openOnClick().then(modifier)
    ) {
        DialogContent()
    }

    @Composable
    private fun ZoomViewModel.projector() = AlertDialogPreferenceProjector(
        interactor = AlertDialogPreferenceInteractor(
            preference = preferenceInteractor(),
            dialog = dialogInteractor()
        )
    )

    @Composable
    private fun ZoomViewModel.preferenceInteractor() = PreferenceInteractor(
        painter = resources.image.painter(),
        title = resources.title.localized(),
        subtitle = resources.subtitle.localized()
    )

    @Composable
    private fun ZoomViewModel.dialogInteractor() = AlertDialogInteractor.Builder(state) {
        logic.clearInput()
    }.triText().build {
        title = resources.title.localized()
        closeOnPositive { logic.onSave() }
        closeOnNeutral { logic.onReset() }
    }

    @Composable
    private fun ZoomViewModel.DialogContent() = Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        pickerProjector().Projection()
    }

    @Composable
    private fun ZoomViewModel.pickerProjector() = PickerProjector<Int>(
        interactor = pickerInteractor()
    )

    @Composable
    private fun ZoomViewModel.pickerInteractor() = IntegerPickerInteractor(
        selected = logic.amount,
        range = logic.amountRange,
        onSelectionChanged = logic.onValueChange,
        upIcon = upIconInteractor(),
        downIcon = downIconInteractor()
    )
}