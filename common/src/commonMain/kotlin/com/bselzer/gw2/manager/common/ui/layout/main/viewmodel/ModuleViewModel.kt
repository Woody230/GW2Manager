package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.configuration.WvwHelper.color
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.main.model.module.WorldModule
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.extension.wvw.owner
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.world.WorldId
import com.bselzer.ktx.resource.Resources
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class ModuleViewModel(context: AppComponentContext) : MainViewModel(context) {
    /**
     * Creates the state for the module displaying the user's choice of world.
     */
    val selectedWorld: WorldModule
        @Composable
        get() {
            val world by repositories.world.selectedWorld().collectAsState(null)
            val match by repositories.wvw.selectedMatch().collectAsState(null)
            val selectedId = world?.id ?: WorldId(0)
            val owner = match?.owner(selectedId) ?: WvwObjectiveOwner.NEUTRAL
            return WorldModule(
                title = Gw2Resources.strings.world.desc(),
                subtitle = worldSubtitle(selectedId, world),
                color = configuration.wvw.color(owner),
                image = Gw2Resources.images.gw2_rank_dolyak,
                description = Gw2Resources.strings.world.desc()
            )
        }

    /**
     * Creates the subtitle of the world using the name of the world if it exists.
     */
    private fun worldSubtitle(id: WorldId, world: World?): StringDesc = when {
        // If we can locate the world, then display the choice.
        world != null -> StringDesc.Raw(world.name.toString())

        // If the user has not selected a world, then use the default message a preference displays.
        id.isDefault -> Resources.strings.not_set.desc()

        // We know that a world has been selected but it currently doesn't exist for some reason.
        else -> Resources.strings.unknown.desc()
    }
}