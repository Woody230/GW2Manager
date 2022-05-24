package com.bselzer.gw2.manager.common.ui.layout.dialog.viewmodel

import androidx.compose.runtime.*
import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.dialog.model.worldselection.NoWorlds
import com.bselzer.gw2.manager.common.ui.layout.dialog.model.worldselection.WorldSelection
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.world.WorldId
import com.bselzer.ktx.settings.compose.nullState
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorldSelectionViewModel(context: AppComponentContext) : DialogViewModel(context) {
    /**
     * All the worlds to display in the dialog.
     */
    private val worlds: List<World>
        @Composable
        get() = repositories.world.worlds()
            .collectAsState(emptyList()).value
            .sortedBy { world -> world.name.toString() }

    /**
     * The state for displaying a message when no worlds are found.
     */
    val noWorlds: NoWorlds
        @Composable
        get() = NoWorlds(
            enabled = worlds.isEmpty(),
            message = Gw2Resources.strings.no_worlds.desc()
        )

    private val selected: MutableState<World?> = mutableStateOf(null)

    /**
     * The state for displaying all worlds and the selection made by the user.
     */
    val selection: WorldSelection
        @Composable
        get() {
            val selectedWorld by preferences.wvw.selectedWorld.nullState()

            // If the dialog has a selection then use it, otherwise use the saved selection.
            val resolved: WorldId? = selected.value?.id ?: selectedWorld
            return WorldSelection(
                title = Gw2Resources.strings.worlds.desc(),
                values = worlds,
                getLabel = { world ->
                    // TODO translate
                    world.name.toString()
                },
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