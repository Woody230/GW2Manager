package com.bselzer.gw2.manager.common.ui.layout.splash.content

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.router.bringToFront
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.AbsoluteBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.common.RelativeBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.host.content.LocalSplashRouter
import com.bselzer.gw2.manager.common.ui.layout.splash.configuration.SplashConfig
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.InitializationViewModel
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.column.ColumnPresenter
import com.bselzer.ktx.compose.ui.layout.description.DescriptionInteractor
import com.bselzer.ktx.compose.ui.layout.description.DescriptionPresenter
import com.bselzer.ktx.compose.ui.layout.description.DescriptionProjector
import com.bselzer.ktx.compose.ui.layout.text.TextPresenter

class InitializationComposition(model: InitializationViewModel) : ViewModelComposition<InitializationViewModel>(model) {
    @Composable
    override fun InitializationViewModel.Content() {
        Initialize()
        Container()
    }

    @Composable
    private fun InitializationViewModel.Initialize() {
        val splashRouter = LocalSplashRouter.current
        Initialize {
            // Don't show the splash screen once initialization is finished.
            splashRouter.bringToFront(SplashConfig.NoSplashConfig)
        }
    }

    @Composable
    private fun InitializationViewModel.Container() = AbsoluteBackgroundImage(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Progress()
        }
    }

    @Composable
    private fun InitializationViewModel.Progress() {
        RelativeBackgroundImage(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            DescriptionProjector(
                interactor = DescriptionInteractor(
                    title = description.value.title.localized(),
                    subtitle = description.value.subtitle?.localized()
                ),
                presenter = DescriptionPresenter(
                    container = ColumnPresenter.CenteredHorizontally,
                    title = TextPresenter(fontSize = 30.sp)
                )
            ).Projection(modifier = Modifier.padding(vertical = 10.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(0.15f)
        )
    }
}