package com.bselzer.gw2.manager.common.ui.layout.host.content

import androidx.compose.runtime.Composable
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.host.model.drawer.DrawerComponent
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.DrawerViewModel
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.ui.layout.drawer.component.DrawerComponentInteractor
import com.bselzer.ktx.compose.ui.layout.drawer.modal.ModalDrawerInteractor
import com.bselzer.ktx.compose.ui.layout.drawer.modal.ModalDrawerProjector
import com.bselzer.ktx.compose.ui.layout.drawer.section.DrawerSectionInteractor
import com.bselzer.ktx.compose.ui.layout.icon.IconInteractor
import com.bselzer.ktx.compose.ui.layout.modifier.interactable.Clickable
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import dev.icerock.moko.resources.compose.localized

class DrawerComposition : ViewModelComposition<DrawerViewModel>() {
    @Composable
    override fun Content(model: DrawerViewModel) = model.run {
        ModalDrawerProjector(
            interactor = ModalDrawerInteractor(
                sections = listOf(wvwSection(), settingsSection(), aboutSection())
            ),
        ).DrawerContent()
    }

    @Composable
    private fun DrawerViewModel.wvwSection() = DrawerSectionInteractor(
        title = TextInteractor(text = wvwTitle.localized()),
        components = listOf(wvwMap.interactor(), wvwMatch.interactor())
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
        val localized: String = description.localized()
        return DrawerComponentInteractor(
            icon = IconInteractor(
                painter = icon.painter(),
                contentDescription = localized
            ),
            text = TextInteractor(text = localized),
            modifier = Clickable {
                // TODO route and close drawer
            }
        )
    }
}