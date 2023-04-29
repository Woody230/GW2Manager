package com.bselzer.gw2.manager.common.ui.layout.host.content

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.FontWeight
import com.arkivanov.decompose.router.stack.bringToFront
import com.bselzer.gw2.manager.common.ui.layout.host.model.drawer.DrawerComponent
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.DrawerViewModel
import com.bselzer.ktx.compose.ui.layout.column.ColumnInteractor
import com.bselzer.ktx.compose.ui.layout.drawer.component.DrawerComponentInteractor
import com.bselzer.ktx.compose.ui.layout.drawer.modal.ModalDrawerInteractor
import com.bselzer.ktx.compose.ui.layout.drawer.modal.ModalDrawerPresenter
import com.bselzer.ktx.compose.ui.layout.drawer.section.DrawerSectionInteractor
import com.bselzer.ktx.compose.ui.layout.drawer.section.DrawerSectionPresenter
import com.bselzer.ktx.compose.ui.layout.icon.IconInteractor
import com.bselzer.ktx.compose.ui.layout.modifier.interactable.Clickable
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import com.bselzer.ktx.compose.ui.layout.text.TextPresenter
import com.bselzer.ktx.compose.ui.layout.text.textInteractor
import com.bselzer.ktx.resource.images.painter
import com.bselzer.ktx.resource.strings.localized

class DrawerComposition(
    private val model: DrawerViewModel
) {
    @Composable
    fun interactor() = model.run {
        ModalDrawerInteractor(
            confirmStateChange = confirmStateChange,
            state = state,
            container = ColumnInteractor.Divided,
            sections = listOf(wvwSection(), settingsSection(), aboutSection())
        )
    }

    @Composable
    fun presenter() = model.run {
        ModalDrawerPresenter(
            section = DrawerSectionPresenter(
                title = TextPresenter(fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary)
            )
        )
    }

    @Composable
    private fun DrawerViewModel.wvwSection() = DrawerSectionInteractor(
        title = TextInteractor(text = wvwTitle.localized()),
        components = listOf(wvwMap.interactor(), wvwMatchContestedAreas.interactor(), wvwMatchOverview.interactor(), wvwMatchStatistics.interactor())
    )

    @Composable
    private fun DrawerViewModel.settingsSection() = DrawerSectionInteractor(
        components = listOf(settings.interactor(), cache.interactor())
    )

    @Composable
    private fun DrawerViewModel.aboutSection() = DrawerSectionInteractor(
        components = listOf(license.interactor(), about.interactor())
    )

    @Composable
    private fun DrawerComponent.interactor(): DrawerComponentInteractor {
        val mainRouter = LocalMainRouter.current
        val scope = rememberCoroutineScope()
        return DrawerComponentInteractor(
            // TODO icons seem to be using disabled tint
            icon = IconInteractor(
                painter = icon.painter(),
                contentDescription = null
            ),
            text = description.textInteractor(),
            modifier = Clickable {
                // Change the current page to the selected destination and close the drawer.
                mainRouter.bringToFront(configuration)
                with(model) { scope.close() }
            }
        )
    }
}