package com.bselzer.gw2.manager.common.ui.layout.host.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.dialog.content.DialogComposition
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.HostViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.content.MainComposition
import com.bselzer.gw2.manager.common.ui.layout.splash.content.SplashComposition

class HostComposition : ViewModelComposition<HostViewModel>() {
    @Composable
    override fun Content(model: HostViewModel) = model.run {
        Core()
        Dialog()
        Splash()
    }

    @Composable
    private fun HostViewModel.Dialog() = Box(
        modifier = Modifier.fillMaxSize()
    ) {
        DialogComposition(dialogRouter).Content()
    }

    @Composable
    private fun HostViewModel.Core() = Box(
        modifier = Modifier.fillMaxSize()
    ) {
        MainComposition(mainRouter, dialogRouter).Content()
    }

    @Composable
    private fun HostViewModel.Splash() = Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        SplashComposition(splashRouter).Content()
    }
}