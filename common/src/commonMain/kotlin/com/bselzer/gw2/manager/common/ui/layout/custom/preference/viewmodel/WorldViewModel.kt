package com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel

import com.bselzer.gw2.manager.common.dependency.ViewModelDependencies
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.WorldResources
import com.bselzer.gw2.v2.model.extension.wvw.owner
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.world.WorldId
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

interface WorldViewModel : ViewModelDependencies {
    val world: World?
    val match: WvwMatch

    /**
     * Creates the state for the module displaying the user's choice of world.
     */
    val worldResources: WorldResources
        get() {
            val selectedId = world?.id ?: WorldId(0)
            val owner = match.owner(selectedId)
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