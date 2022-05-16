package com.bselzer.gw2.manager.common.ui.layout.dialog.content

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.router.Router
import com.arkivanov.decompose.router.bringToFront
import com.bselzer.gw2.manager.common.ui.base.RouterComposition
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.DialogViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.NoDialogViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.WorldSelectionViewModel

class DialogComposition(
    router: Router<DialogConfig, DialogViewModel>
) : RouterComposition<DialogConfig, DialogViewModel>(router) {
    @Composable
    override fun DialogViewModel.Content() = when (this) {
        is NoDialogViewModel -> {}
        is WorldSelectionViewModel -> WorldSelectionComposition {
            // Don't show the dialog anymore when the world has been selected.
            router.bringToFront(DialogConfig.NoDialogConfig)
        }.Content(this)
    }
}