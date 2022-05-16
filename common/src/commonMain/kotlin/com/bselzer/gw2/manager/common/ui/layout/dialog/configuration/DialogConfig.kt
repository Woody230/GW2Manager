package com.bselzer.gw2.manager.common.ui.layout.dialog.configuration

import com.arkivanov.essenty.parcelable.Parcelize
import com.bselzer.gw2.manager.common.ui.base.Configuration

sealed class DialogConfig : Configuration {
    @Parcelize
    object NoDialogConfig : DialogConfig()

    @Parcelize
    object WorldSelectionConfig : DialogConfig()
}