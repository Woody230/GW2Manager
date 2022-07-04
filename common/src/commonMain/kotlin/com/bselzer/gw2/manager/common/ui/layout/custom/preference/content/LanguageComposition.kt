package com.bselzer.gw2.manager.common.ui.layout.custom.preference.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.LanguageViewModel
import com.bselzer.gw2.manager.common.ui.theme.ThemedColorFilter
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.resource.ui.layout.alertdialog.triText
import com.bselzer.ktx.compose.ui.layout.alertdialog.AlertDialogInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.DialogState
import com.bselzer.ktx.compose.ui.layout.alertdialog.openOnClick
import com.bselzer.ktx.compose.ui.layout.alertdialog.singlechoice.SingleChoiceInteractor
import com.bselzer.ktx.compose.ui.layout.alertdialog.singlechoice.SingleChoiceProjector
import com.bselzer.ktx.compose.ui.layout.image.ImagePresenter
import com.bselzer.ktx.compose.ui.layout.preference.PreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.PreferencePresenter
import com.bselzer.ktx.compose.ui.layout.preference.alertdialog.AlertDialogPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.alertdialog.AlertDialogPreferencePresenter
import com.bselzer.ktx.compose.ui.layout.preference.alertdialog.AlertDialogPreferenceProjector
import com.bselzer.ktx.intl.Locale

class LanguageComposition(
    model: LanguageViewModel,
    private val state: MutableState<DialogState>
) : ViewModelComposition<LanguageViewModel>(model) {

    @Composable
    override fun LanguageViewModel.Content(modifier: Modifier) = projector().Projection(
        modifier = state.openOnClick().then(modifier)
    ) {
        singleChoiceProjector().Projection()
    }

    @Composable
    private fun LanguageViewModel.projector() = AlertDialogPreferenceProjector(
        presenter = presenter(),
        interactor = interactor()
    )

    @Composable
    private fun LanguageViewModel.presenter() = AlertDialogPreferencePresenter(
        preference = PreferencePresenter(
            // Language image is by default harder to see in dark mode.
            image = ImagePresenter(colorFilter = ThemedColorFilter)
        )
    )

    @Composable
    private fun LanguageViewModel.interactor() = AlertDialogPreferenceInteractor(
        preference = preferenceInteractor(),
        dialog = dialogInteractor()
    )

    @Composable
    private fun LanguageViewModel.preferenceInteractor() = PreferenceInteractor(
        painter = resources.image.painter(),
        title = resources.title.localized(),
        subtitle = resources.subtitle.localized()
    )

    @Composable
    private fun LanguageViewModel.dialogInteractor() = AlertDialogInteractor.Builder(state) {
        logic.resetSelection()
    }.triText().build {
        title = resources.title.localized()
        closeOnPositive { logic.onSave() }
        closeOnNeutral { logic.onReset() }
    }

    @Composable
    private fun LanguageViewModel.singleChoiceProjector() = SingleChoiceProjector<Locale>(
        interactor = singleChoiceInteractor()
    )

    @Composable
    private fun LanguageViewModel.singleChoiceInteractor(): SingleChoiceInteractor<Locale> {
        // TODO Desktop: lazy column inside alert dialog crash https://github.com/JetBrains/compose-jb/issues/1111
        val labels = labels
        return SingleChoiceInteractor(
            selected = logic.selected(),
            values = logic.values.sortedBy { locale -> labels[locale] },
            getLabel = { locale -> labels[locale] ?: "" },
            onSelection = { locale -> logic.updateSelection(locale) }
        )
    }
}