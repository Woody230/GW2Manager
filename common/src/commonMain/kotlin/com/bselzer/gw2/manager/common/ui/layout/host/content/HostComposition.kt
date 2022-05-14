package com.bselzer.gw2.manager.common.ui.layout.host.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.pop
import com.bselzer.gw2.manager.common.ui.base.Composition
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.HostViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.content.SplashComposition

class HostComposition : Composition<HostViewModel> {
    @Composable
    override fun Content(model: HostViewModel) = model.run {
        splash()
    }

    @Composable
    private fun HostViewModel.splash() = Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        SplashComposition {
            // Don't show the splash screen once initialization is finished.
            splashRouter.pop()
        }.Content(state = splashRouter.state)
    }
}