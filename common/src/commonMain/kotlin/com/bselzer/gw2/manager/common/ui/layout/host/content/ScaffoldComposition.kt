package com.bselzer.gw2.manager.common.ui.layout.host.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.toUpperCase
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.repository.data.generic.Gw2ApiStatusType
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.dialog.content.DialogComposition
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.ScaffoldViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.content.MainComposition
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.WvwMatchOverviewViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.content.SplashComposition
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.ktx.compose.ui.intl.LocalLocale
import com.bselzer.ktx.compose.ui.layout.appbar.top.TopAppBarInteractor
import com.bselzer.ktx.compose.ui.layout.drawer.modal.ModalDrawerPresenter
import com.bselzer.ktx.compose.ui.layout.floatingactionbutton.FloatingActionButtonInteractor
import com.bselzer.ktx.compose.ui.layout.floatingactionbutton.FloatingActionButtonPresenter
import com.bselzer.ktx.compose.ui.layout.icon.drawerNavigationIconInteractor
import com.bselzer.ktx.compose.ui.layout.icon.dropdownIconInteractor
import com.bselzer.ktx.compose.ui.layout.iconbutton.IconButtonInteractor
import com.bselzer.ktx.compose.ui.layout.merge.TriState
import com.bselzer.ktx.compose.ui.layout.scaffold.ScaffoldInteractor
import com.bselzer.ktx.compose.ui.layout.scaffold.ScaffoldPresenter
import com.bselzer.ktx.compose.ui.layout.scaffold.ScaffoldProjector
import com.bselzer.ktx.compose.ui.layout.scaffold.scaffoldInteractor
import com.bselzer.ktx.compose.ui.layout.snackbar.SnackbarPresenter
import com.bselzer.ktx.compose.ui.layout.snackbarhost.LocalSnackbarHostState
import com.bselzer.ktx.compose.ui.layout.snackbarhost.SnackbarHostInteractor
import com.bselzer.ktx.compose.ui.layout.snackbarhost.SnackbarHostPresenter
import com.bselzer.ktx.compose.ui.layout.text.textInteractor
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.format

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
            ),
            floatingActionButton = floatingActionButtonInteractor()
        ),
        presenter = ScaffoldPresenter(
            drawer = drawerPresenter,
            floatingActionButton = floatingActionButtonPresenter(),
            snackbarHost = snackbarHostPresenter()
        )
    ).Projection(modifier = Modifier.fillMaxSize().then(modifier)) {
        mainComposition.Content()
        DialogComposition().Content()
        Splash()
    }

    @Composable
    private fun ScaffoldViewModel.floatingActionButtonInteractor(): FloatingActionButtonInteractor? {
        // Only display the status on the overview screen.
        if (LocalMainRouter.current.activeChild.instance !is WvwMatchOverviewViewModel) {
            return null
        }

        val status = repositories.status.status.value
        val message = when (status.type) {
            Gw2ApiStatusType.Available -> AppResources.strings.status_available_description.localized()
            Gw2ApiStatusType.Unavailable -> AppResources.strings.status_unavailable_description.format(status.message).localized()
        }

        var shouldShowSnackbar by remember { mutableStateOf(false) }
        val host = LocalSnackbarHostState.current
        val dismiss = KtxResources.strings.dismiss.localized().toUpperCase(LocalLocale.current)
        LaunchedEffect(shouldShowSnackbar) {
            if (shouldShowSnackbar) {
                host.showSnackbar(message, actionLabel = dismiss, duration = SnackbarDuration.Indefinite)
                shouldShowSnackbar = false
            }
        }

        return FloatingActionButtonInteractor(
            text = status.desc().textInteractor(),
            onClick = {
                shouldShowSnackbar = true
            }
        )
    }

    @Composable
    private fun ScaffoldViewModel.snackbarHostPresenter() = SnackbarHostPresenter(
        snackbar = snackbarPresenter()
    )

    @Composable
    private fun ScaffoldViewModel.snackbarPresenter() = SnackbarPresenter(
        actionOnNewLine = TriState.TRUE
    )

    @Composable
    private fun ScaffoldViewModel.floatingActionButtonPresenter(): FloatingActionButtonPresenter {
        val status = repositories.status.status.value
        return FloatingActionButtonPresenter(
            backgroundColor = when (status.type) {
                Gw2ApiStatusType.Available -> WvwObjectiveOwner.GREEN.color()
                Gw2ApiStatusType.Unavailable -> WvwObjectiveOwner.RED.color()
            },
            contentColor = Color.Black
        )
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