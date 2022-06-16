package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.repository.instance.specialized.SelectedWorldData
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.main.model.action.SelectedWorldRefreshAction.Companion.refreshAction
import com.bselzer.gw2.manager.common.ui.layout.main.model.module.WorldResources
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.extension.wvw.owner
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.world.WorldId
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class ModuleViewModel(
    context: AppComponentContext
) : MainViewModel(context), SelectedWorldData by context.repositories.selectedWorld {
    override val title: StringDesc = AppResources.strings.app_name.desc()

    override val actions
        get() = listOf(refreshAction())

    /**
     * Creates the state for the module displaying the user's choice of world.
     */
    val selectedWorld: WorldResources
        get() {
            val selectedId = world?.id ?: WorldId(0)
            val owner = match?.owner(selectedId) ?: WvwObjectiveOwner.NEUTRAL
            return WorldResources(
                title = Gw2Resources.strings.world.desc(),
                subtitle = worldSubtitle(selectedId, world),
                color = owner.color(),
                image = Gw2Resources.images.rank_dolyak,
            )
        }

    /**
     * Creates the subtitle of the world using the name of the world if it exists.
     */
    private fun worldSubtitle(id: WorldId, world: World?): StringDesc = when {
        // If we can locate the world, then display the choice.
        world != null -> world.name.value.translated().desc()

        // If the user has not selected a world, then use the default message a preference displays.
        id.isDefault -> KtxResources.strings.not_set.desc()

        // We know that a world has been selected but it currently doesn't exist for some reason.
        else -> KtxResources.strings.unknown.desc()
    }
}