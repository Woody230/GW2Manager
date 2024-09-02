package com.bselzer.gw2.manager.common.ui.layout.dialog.configuration

import com.bselzer.gw2.manager.common.ui.base.Configuration
import kotlinx.serialization.Serializable

@Serializable
sealed class DialogConfig : Configuration {
    @Serializable
    object NoDialogConfig : DialogConfig()

    @Serializable
    object WorldSelectionConfig : DialogConfig()
}