package com.bselzer.gw2.manager.common.ui.layout.host.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.host.viewmodel.HostViewModel

class HostComposition : ViewModelComposition<HostViewModel>() {
    @Composable
    override fun Content(model: HostViewModel) = model.run {
        registerOnBackPressed()

        CompositionLocalProvider(
            LocalDialogRouter provides model.dialogRouter,
            LocalMainRouter provides model.mainRouter,
            LocalSplashRouter provides model.splashRouter
        ) {
            ScaffoldComposition().Content(scaffold)
        }
    }

    @Composable
    private fun HostViewModel.registerOnBackPressed() {
        val scope = rememberCoroutineScope()
        backPressedHandler.register { onBackPressed(scope) }
    }
}