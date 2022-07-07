package com.bselzer.gw2.manager.common.ui.layout.host.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.dialog.content.DialogComposition
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.ScaffoldViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.content.MainComposition
import com.bselzer.gw2.manager.common.ui.layout.splash.content.SplashComposition
import com.bselzer.ktx.compose.ui.layout.appbar.top.TopAppBarInteractor
import com.bselzer.ktx.compose.ui.layout.drawer.modal.ModalDrawerPresenter
import com.bselzer.ktx.compose.ui.layout.icon.drawerNavigationIconInteractor
import com.bselzer.ktx.compose.ui.layout.icon.dropdownIconInteractor
import com.bselzer.ktx.compose.ui.layout.iconbutton.IconButtonInteractor
import com.bselzer.ktx.compose.ui.layout.scaffold.ScaffoldInteractor
import com.bselzer.ktx.compose.ui.layout.scaffold.ScaffoldPresenter
import com.bselzer.ktx.compose.ui.layout.scaffold.ScaffoldProjector
import com.bselzer.ktx.compose.ui.layout.scaffold.scaffoldInteractor
import com.bselzer.ktx.compose.ui.layout.snackbarhost.SnackbarHostInteractor

class ScaffoldComposition(model: ScaffoldViewModel) : ViewModelComposition<ScaffoldViewModel>(model) {
    @Composable
    override fun ScaffoldViewModel.Content(modifier: Modifier) {
        val drawer = DrawerComposition(drawer)
        scaffoldInteractor(
            drawer = drawer.interactor(),
            snackbarHost = SnackbarHostInteractor()
        ) { interactor ->
            Scaffold(
                modifier = modifier,
                scaffoldInteractor = interactor,
                drawerPresenter = drawer.presenter(),
                mainComposition = MainComposition()
            )
        }
    }

    @Composable
    private fun ScaffoldViewModel.Scaffold(
        modifier: Modifier,
        scaffoldInteractor: ScaffoldInteractor,
        drawerPresenter: ModalDrawerPresenter,
        mainComposition: MainComposition
    ) = ScaffoldProjector(
        interactor = scaffoldInteractor.copy(
            topBar = TopAppBarInteractor(
                title = mainComposition.title(),
                navigation = navigationInteractor(),
                actions = mainComposition.actions(),
                dropdown = dropdownIconInteractor()
            )
        ),
        presenter = ScaffoldPresenter(
            drawer = drawerPresenter
        )
    ).Projection(modifier = Modifier.fillMaxSize().then(modifier)) {
        mainComposition.Content()
        DialogComposition().Content()
        Splash()
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