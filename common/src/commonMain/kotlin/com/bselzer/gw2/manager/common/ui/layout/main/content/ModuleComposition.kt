package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.router.bringToFront
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.host.content.LocalDialogRouter
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.ModuleViewModel
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.background.image.BackgroundImage
import com.bselzer.ktx.compose.ui.layout.preference.text.TextPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.text.TextPreferencePresenter
import com.bselzer.ktx.compose.ui.layout.preference.text.TextPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.text.TextPresenter

class ModuleComposition(model: ModuleViewModel) : MainChildComposition<ModuleViewModel>(model) {
    @Composable
    override fun ModuleViewModel.Content() = BackgroundImage(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
        painter = absoluteBackgroundPainter,
        presenter = absoluteBackgroundPresenter
    ) {
        Column(
            modifier = Modifier.padding(paddingValues).verticalScroll(rememberScrollState())
        ) {
            SelectedWorld()
        }
    }

    /**
     * Lays out the selected world with the ability to show the dialog for a new selection.
     */
    @Composable
    private fun ModuleViewModel.SelectedWorld() = ModuleCard {
        val dialogRouter = LocalDialogRouter.current
        TextPreferenceProjector(
            interactor = TextPreferenceInteractor(
                painter = selectedWorld.image.painter(),
                title = selectedWorld.title.localized(),
                subtitle = selectedWorld.subtitle.localized(),
            ),
            presenter = TextPreferencePresenter(
                subtitle = TextPresenter(color = selectedWorld.color, fontWeight = FontWeight.Bold)
            )
        ).Projection(modifier = Modifier.clickable {
            // Open up the world selection dialog so that the user can pick another world.
            dialogRouter.bringToFront(DialogConfig.WorldSelectionConfig)
        })
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
            BackgroundImage(
                painter = relativeBackgroundPainter,
                presenter = relativeBackgroundPresenter,
                content = content
            )
        }
    }
}