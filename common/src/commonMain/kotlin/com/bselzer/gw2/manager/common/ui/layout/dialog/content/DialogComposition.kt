package com.bselzer.gw2.manager.common.ui.layout.dialog.content

import androidx.compose.runtime.Composable
import com.bselzer.gw2.manager.common.ui.base.RouterComposition
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.DialogViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.NoDialogViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel.WorldSelectionViewModel
import com.bselzer.gw2.manager.common.ui.layout.host.content.LocalDialogRouter

class DialogComposition : RouterComposition<DialogConfig, DialogViewModel>(
    router = { LocalDialogRouter.current }
) {
    @Composable
    override fun DialogViewModel.Content() = when (this) {
        is NoDialogViewModel -> {}
        is WorldSelectionViewModel -> WorldSelectionComposition().Content(this)
    }
}