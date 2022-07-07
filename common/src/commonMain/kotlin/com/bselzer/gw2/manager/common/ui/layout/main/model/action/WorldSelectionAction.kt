package com.bselzer.gw2.manager.common.ui.layout.main.model.action

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.ktx.compose.ui.layout.icon.IconInteractor
import com.bselzer.ktx.resource.images.painter
import com.bselzer.ktx.resource.strings.localized
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.CoroutineScope

class WorldSelectionAction(
    showDialog: (DialogConfig) -> Unit
) : AppBarAction {
    override val enabled: Boolean = true
    override val icon: @Composable () -> IconInteractor = {
        IconInteractor(
            painter = Icons.Filled.List.painter(),
            contentDescription = Gw2Resources.strings.world.localized(),
        )
    }
    override val notification: StringDesc? = null
    override val onClick: suspend CoroutineScope.() -> Unit = {
        showDialog(DialogConfig.WorldSelectionConfig)
    }
}