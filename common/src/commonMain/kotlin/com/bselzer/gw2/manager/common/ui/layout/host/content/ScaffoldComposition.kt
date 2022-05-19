package com.bselzer.gw2.manager.common.ui.layout.host.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.dialog.content.DialogComposition
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.ScaffoldViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.content.MainComposition
import com.bselzer.gw2.manager.common.ui.layout.splash.content.SplashComposition
import com.bselzer.ktx.compose.resource.ui.layout.icon.drawerNavigationIconInteractor
import com.bselzer.ktx.compose.ui.layout.iconbutton.IconButtonInteractor
import com.bselzer.ktx.compose.ui.layout.scaffold.ScaffoldInteractor
import com.bselzer.ktx.compose.ui.layout.scaffold.ScaffoldPresenter
import com.bselzer.ktx.compose.ui.layout.scaffold.ScaffoldProjector
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import com.bselzer.ktx.compose.ui.layout.topappbar.TopAppBarInteractor
import dev.icerock.moko.resources.compose.localized

class ScaffoldComposition : ViewModelComposition<ScaffoldViewModel>() {
    @Composable
    override fun Content(model: ScaffoldViewModel) = model.run {
        val drawer = DrawerComposition(drawer)
        val mainModel = LocalMainRouter.current.state.subscribeAsState().value.activeChild.instance
        ScaffoldProjector(
            interactor = ScaffoldInteractor(
                drawer = drawer.interactor(),
                topBar = TopAppBarInteractor(
                    title = TextInteractor(text = mainModel.title.localized()),
                    navigation = navigationInteractor(),
                    actions = mainModel.actions()
                )
            ),
            presenter = ScaffoldPresenter(
                drawer = drawer.presenter()
            )
        ).Projection(modifier = Modifier.fillMaxSize()) {
            MainComposition().Content()
            DialogComposition().Content()
            Splash()
        }
    }

    @Composable
    private fun ScaffoldViewModel.navigationInteractor() = with(rememberCoroutineScope()) {
        IconButtonInteractor(
            icon = drawerNavigationIconInteractor()
        ) {
            // Open the drawer when the icon is clicked.
            with(drawer) { open() }
        }
    }

    @Composable
    private fun ScaffoldViewModel.Splash() = Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        SplashComposition().Content()
    }
}