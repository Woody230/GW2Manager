package com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.dialog.model.worldselection.NoWorlds
import com.bselzer.gw2.manager.common.ui.layout.dialog.model.worldselection.WorldSelection
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.world.WorldId
import com.bselzer.gw2.v2.resource.Gw2Resources
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorldSelectionViewModel(context: AppComponentContext) : DialogViewModel(context) {
    /**
     * All the worlds to display in the dialog.
     */
    private val worlds: List<World>
        get() = repositories.world.worlds.values.sortedBy { world -> world.name.toString() }

    /**
     * The state for displaying a message when no worlds are found.
     */
    val noWorlds: NoWorlds
        get() = NoWorlds(
            enabled = worlds.isEmpty(),
            message = AppResources.strings.no_worlds.desc()
        )

    private val selected: MutableState<World?> = mutableStateOf(null)

    /**
     * The state for displaying all worlds and the selection made by the user.
     */
    val selection: WorldSelection
        get() {
            val selectedWorldId = repositories.selectedWorld.worldId

            // If the dialog has a selection then use it, otherwise use the saved selection.
            val resolved: WorldId? = selected.value?.id ?: selectedWorldId
            return WorldSelection(
                title = Gw2Resources.strings.worlds.desc(),
                values = worlds,
                getLabel = { world -> world.name.toString().translated() },
                selected = resolved?.let {
                    worlds.firstOrNull { world -> world.id == it }
                },
                onSave = { selection ->
                    CoroutineScope(Dispatchers.Main).launch {
                        preferences.wvw.selectedWorld.set(selection.id)
                    }
                },
                onReset = {
                    CoroutineScope(Dispatchers.Main).launch {
                        preferences.wvw.selectedWorld.remove()
                    }
                },
                setSelected = { selected.value = it },
                resetSelected = { selected.value = null }
            )
        }
}