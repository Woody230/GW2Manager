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
import com.bselzer.ktx.logging.Logger
import dev.icerock.moko.resources.desc.desc
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
        get() = WorldSelection(
            title = Gw2Resources.strings.worlds.desc(),
            values = worlds,
            getLabel = { world -> world.name.toString().translated() },
            selected = resolvedId?.let {
                worlds.firstOrNull { world -> world.id == it }
            },
            onSave = { selection ->
                scope.launch {
                    preferences.wvw.selectedWorld.set(selection.id)
                }
            },
            onReset = {
                scope.launch {
                    preferences.wvw.selectedWorld.remove()
                }
            },
            setSelected = {
                Logger.d { "World | Selection | Selected $it" }
                selected.value = it
            },
            resetSelected = { selected.value = null }
        )

    private val resolvedId: WorldId?
        get() {
            val resolvedId: WorldId?

            // If the dialog has a selection then use it, otherwise use the saved selection.
            val selectedWorldId = selected.value?.id
            if (selectedWorldId != null) {
                Logger.d { "World | Selection | Using dialog selected id of $selectedWorldId" }
                resolvedId = selectedWorldId
            }
            else {
                val savedWorldId = repositories.selectedWorld.worldId
                Logger.d { "World | Selection | Using saved id of $savedWorldId"}
                resolvedId = savedWorldId
            }

            return resolvedId
        }
}