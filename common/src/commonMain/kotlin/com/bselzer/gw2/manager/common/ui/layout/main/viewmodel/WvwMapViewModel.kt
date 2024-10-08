package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import com.arkivanov.decompose.router.stack.bringToFront
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.Router
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.main.configuration.MapConfig
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.AppBarAction
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.MapViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ObjectiveViewModel
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map.ViewerViewModel
import com.bselzer.gw2.v2.model.wvw.objective.WvwMapObjectiveId
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.serialization.serializer

class WvwMapViewModel(
    context: AppComponentContext,
    private val showDialog: (DialogConfig) -> Unit
) : MainViewModel(context) {
    val router: Router<MapConfig, MapViewModel> = createRouter(
        initialStack = { listOf(MapConfig.ViewerConfig) },
        serializer = serializer(),
        key = "Map",
        childFactory = { state, context ->
            when (state) {
                is MapConfig.ObjectiveConfig -> ObjectiveViewModel(context, WvwMapObjectiveId(state.id), showDialog)
                is MapConfig.ViewerConfig -> ViewerViewModel(context, showDialog)
            }
        }
    )

    override val title: StringDesc
        get() = router.activeChild.instance.title

    override val actions: List<AppBarAction>
        get() = router.activeChild.instance.actions

    /**
     * Handles back navigation between the routers.
     *
     * @return whether the back press is handled
     */
    override fun onBackPressed(): Boolean = when (router.activeChild.instance) {
        // If we aren't on the map viewer then go back to it.
        !is ViewerViewModel -> {
            router.bringToFront(MapConfig.ViewerConfig)
            true
        }

        // Otherwise let the main back handler propagate backing out.
        else -> false
    }
}