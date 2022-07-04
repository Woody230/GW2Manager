package com.bselzer.gw2.manager.common.ui.layout.custom.objective.viewmodel

import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.objective.model.CoreMapData
import com.bselzer.gw2.manager.common.ui.layout.custom.objective.model.CoreMatchData
import com.bselzer.gw2.manager.common.ui.layout.custom.owner.model.Owner
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveType
import com.bselzer.gw2.v2.model.enumeration.extension.decodeOrNull
import com.bselzer.gw2.v2.model.extension.wvw.owner
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.resource.strings.stringDesc
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format

class CoreMatchViewModel(
    context: AppComponentContext,
    objective: WvwObjective,
    matchObjective: WvwMapObjective?
) : ViewModel(context), CoreMatchData {
    private val objectiveName: StringDesc = objective.name.translated().desc()
    private val type: StringDesc = (objective.type.decodeOrNull() ?: WvwObjectiveType.GENERIC).stringDesc()
    override val name: StringDesc = AppResources.strings.overview_name.format(objectiveName, type)

    override val flipped: StringDesc? = matchObjective?.lastFlippedAt?.let { lastFlippedAt ->
        configuration.wvw.flippedAt(lastFlippedAt)
    }

    override val map: CoreMapData? = objective.mapType.decodeOrNull()?.let { mapType ->
        CoreMapData(
            name = mapType.stringDesc(),
            color = mapType.owner().color()
        )
    }

    override val owner: Owner? = matchObjective?.owner?.decodeOrNull()?.let { owner ->
        Owner(
            name = repositories.selectedWorld.displayableLinkedWorlds(owner),
            color = owner.color()
        )
    }
}