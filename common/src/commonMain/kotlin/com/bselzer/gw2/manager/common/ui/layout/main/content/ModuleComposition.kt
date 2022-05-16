package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.ModuleViewModel
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.ui.layout.background.image.BackgroundImage
import com.bselzer.ktx.compose.ui.layout.image.ImageInteractor
import com.bselzer.ktx.compose.ui.layout.preference.text.TextPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.text.TextPreferencePresenter
import com.bselzer.ktx.compose.ui.layout.preference.text.TextPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import com.bselzer.ktx.compose.ui.layout.text.TextPresenter
import dev.icerock.moko.resources.compose.localized

class ModuleComposition : ViewModelComposition<ModuleViewModel>() {
    @Composable
    override fun Content(model: ModuleViewModel) = model.run {
        BackgroundImage(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
            painter = absoluteBackgroundPainter,
        ) {
            Column(modifier = Modifier.padding(25.dp)) {
                // TODO top app bar with refresh action
                SelectedWorld()
            }
        }
    }

    /**
     * Lays out the selected world with the ability to show the dialog for a new selection.
     */
    @Composable
    private fun ModuleViewModel.SelectedWorld() = ModuleCard {
        // TODO on click: show world selection dialog
        // Open up the world selection dialog so that the user can pick another world.
        TextPreferenceProjector(
            interactor = TextPreferenceInteractor(
                image = ImageInteractor(painter = selectedWorld.image.painter(), contentDescription = selectedWorld.description.localized()),
                title = TextInteractor(selectedWorld.title.localized()),
                subtitle = TextInteractor(selectedWorld.subtitle.localized()),
            ),
            presenter = TextPreferencePresenter(
                subtitle = TextPresenter(color = selectedWorld.color, fontWeight = FontWeight.Bold)
            )
        ).Projection()
    }

    /**
     * Lays out a card wrapping the underlying [content].
     */
    @Composable
    private fun ModuleCard(content: @Composable BoxScope.() -> Unit) {
        val border = 3.dp
        Card(
            elevation = 10.dp,
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth(.90f)
                .wrapContentHeight()
                .border(width = border, color = Color.Black)
                .padding(all = border)
        ) {
            BackgroundImage(painter = relativeBackgroundPainter, content = content)
        }
    }
}