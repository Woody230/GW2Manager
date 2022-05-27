package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map

import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.Action
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.WorldSelectionAction
import com.bselzer.ktx.compose.resource.ui.layout.icon.refreshIconInteractor
import com.bselzer.ktx.logging.Logger
import dev.icerock.moko.resources.desc.StringDesc

sealed class MapViewModel(
    context: AppComponentContext,
    private val showDialog: (DialogConfig) -> Unit
) : ViewModel(context) {
    abstract val title: StringDesc

    private val refreshAction
        get() = Action(
            icon = { refreshIconInteractor() },
            onClick = {
                repositories.world.worlds().collect { worlds ->
                    Logger.d { "Manual Refresh | Worlds: $worlds" }
                }

                repositories.wvw.selectedMatch().collect { match ->
                    Logger.d { "Manual Refresh | Match: $match" }
                }

                // TODO continents, tiles, upgrades, guild upgrades
            }
        )

    private val worldSelectionAction
        get() = WorldSelectionAction(showDialog).action

    open val actions: List<Action> = listOf(refreshAction, worldSelectionAction)
}