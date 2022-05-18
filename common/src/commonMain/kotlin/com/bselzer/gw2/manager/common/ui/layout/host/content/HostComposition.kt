package com.bselzer.gw2.manager.common.ui.layout.host.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.dialog.content.DialogComposition
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.HostViewModel
import com.bselzer.gw2.manager.common.ui.layout.splash.content.SplashComposition

class HostComposition : ViewModelComposition<HostViewModel>() {
    @Composable
    override fun Content(model: HostViewModel) = model.run {
        registerOnBackPressed()

        CompositionLocalProvider(
            LocalDialogRouter provides model.dialogRouter,
            LocalMainRouter provides model.mainRouter,
            LocalSplashRouter provides model.splashRouter
        ) {
            Core()
            Dialog()
            Splash()
        }
    }

    @Composable
    private fun HostViewModel.registerOnBackPressed() {
        val scope = rememberCoroutineScope()
        backPressedHandler.register { onBackPressed(scope) }
    }

    @Composable
    private fun HostViewModel.Dialog() = Box(
        modifier = Modifier.fillMaxSize()
    ) {
        DialogComposition().Content()
    }

    @Composable
    private fun HostViewModel.Core() {
        ScaffoldComposition().Content(scaffold)
    }

    @Composable
    private fun HostViewModel.Splash() = Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        SplashComposition().Content()
    }
}