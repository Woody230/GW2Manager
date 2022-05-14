package com.bselzer.gw2.manager.common.ui.layout.host.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.HostViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.content.SplashComposition

class HostComposition : ViewModelComposition<HostViewModel>() {
    @Composable
    override fun Content(model: HostViewModel) = model.run {
        splash()
    }

    @Composable
    private fun HostViewModel.splash() = Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        SplashComposition(splashRouter).Content()
    }
}