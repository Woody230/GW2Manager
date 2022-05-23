package com.bselzer.gw2.manager.common.ui.layout.host.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.HostViewModel

class HostComposition(model: HostViewModel) : ViewModelComposition<HostViewModel>(model) {
    @Composable
    override fun HostViewModel.Content() {
        registerOnBackPressed()

        CompositionLocalProvider(
            LocalDialogRouter provides dialogRouter,
            LocalMainRouter provides mainRouter,
            LocalSplashRouter provides splashRouter
        ) {
            ScaffoldComposition(scaffold).Content()
        }
    }

    @Composable
    private fun HostViewModel.registerOnBackPressed() {
        val scope = rememberCoroutineScope()
        backPressedHandler.register { onBackPressed(scope) }
    }
}