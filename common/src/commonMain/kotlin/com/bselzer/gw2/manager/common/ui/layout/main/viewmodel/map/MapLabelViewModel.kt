package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.repository.data.specialized.SelectedWorldData
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.v2.model.enumeration.WvwMapType
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.extension.decodeOrNull
import com.bselzer.gw2.v2.model.extension.wvw.linkedWorlds
import com.bselzer.gw2.v2.model.extension.wvw.owner
import com.bselzer.gw2.v2.model.map.Map
import com.bselzer.gw2.v2.model.wvw.map.WvwMap
import com.bselzer.gw2.v2.resource.strings.stringDesc
import com.bselzer.gw2.v2.tile.model.position.BoundedPosition
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class MapLabelViewModel(
    context: AppComponentContext,
    wvwMap: WvwMap,
    map: Map
) : ViewModel(context), SelectedWorldData by context.repositories.selectedWorld {
    private val type: WvwMapType? = wvwMap.type.decodeOrNull()

    private val owner: WvwObjectiveOwner = type?.owner() ?: WvwObjectiveOwner.NEUTRAL
    val color: Color = owner.color()

    private val topLeft: BoundedPosition = grid.bounded(map.continentRectangle.topLeft)
    private val topRight: BoundedPosition = grid.bounded(map.continentRectangle.topRight)
    val position: BoundedPosition = topLeft
    val width: Double = topRight.x - topLeft.x

    val description: StringDesc = when {
        // If there are worlds then display them.
        match.linkedWorlds(owner).isNotEmpty() -> displayableLinkedWorlds(owner)

        // Otherwise fall back to the map name.
        type != null -> type.stringDesc()
        else -> map.name.translated().desc()
    }
}