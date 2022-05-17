package com.bselzer.gw2.manager.common.ui.layout.host.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.host.model.drawer.DrawerComponent
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.DrawerViewModel
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.ui.layout.icon.IconInteractor
import com.bselzer.ktx.compose.ui.layout.icon.IconPresenter
import com.bselzer.ktx.compose.ui.layout.icontext.IconTextInteractor
import com.bselzer.ktx.compose.ui.layout.icontext.IconTextPresenter
import com.bselzer.ktx.compose.ui.layout.modaldrawer.ModalDrawerInteractor
import com.bselzer.ktx.compose.ui.layout.modaldrawer.ModalDrawerPresenter
import com.bselzer.ktx.compose.ui.layout.modaldrawer.ModalDrawerProjector
import com.bselzer.ktx.compose.ui.layout.modifier.interactable.Clickable
import com.bselzer.ktx.compose.ui.layout.modifier.presentable.ModularSize
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import dev.icerock.moko.resources.compose.localized

class DrawerComposition : ViewModelComposition<DrawerViewModel>() {
    @Composable
    override fun Content(model: DrawerViewModel) = model.run {
        ModalDrawerProjector(
            interactor = ModalDrawerInteractor(
                components = icons.map { icons -> icons.map { icon -> icon.iconTextInteractor() } }
            ),
            presenter = ModalDrawerPresenter(
                component = IconTextPresenter(
                    icon = IconPresenter(modifier = ModularSize(24.dp, 24.dp))
                )
            )
        ).drawerContent()
    }

    @Composable
    private fun DrawerComponent.iconTextInteractor(): IconTextInteractor {
        val localized: String = description.localized()
        return IconTextInteractor(
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