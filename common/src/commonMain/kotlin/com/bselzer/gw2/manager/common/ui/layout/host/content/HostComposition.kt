package com.bselzer.gw2.manager.common.ui.layout.host.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.backhandler.BackCallback
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.HostViewModel

class HostComposition(model: HostViewModel) : ViewModelComposition<HostViewModel>(model) {
    @Composable
    override fun HostViewModel.Content(modifier: Modifier) {
        registerOnBackPressed()

        CompositionLocalProvider(
            LocalDialogRouter provides dialogRouter,
            LocalMainRouter provides mainRouter,
            LocalSplashRouter provides splashRouter,
        ) {
            ScaffoldComposition(scaffold).Content(modifier)
        }

        repositories.selectedWorld.Refresh()
        repositories.selectedWorld.UpdateWorld()
    }

    @Composable
    private fun HostViewModel.registerOnBackPressed() {
        val scope = rememberCoroutineScope()
        val callback = BackCallback { onBackPressed(scope) }
        backHandler.register(callback)
    }
}