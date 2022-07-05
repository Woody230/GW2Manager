package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map

import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.repository.data.specialized.SelectedWorldData
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveOwner
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveType
import com.bselzer.gw2.v2.model.enumeration.extension.decodeOrNull
import com.bselzer.gw2.v2.model.extension.wvw.objective
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.resource.strings.stringDesc
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format

class SelectedLabelViewModel(
    context: AppComponentContext,
    selected: WvwObjective
) : ViewModel(context), SelectedWorldData by context.repositories.selectedWorld {
    private val matchObjective = match.objective(selected)
    private val owner = matchObjective?.owner?.decodeOrNull() ?: WvwObjectiveOwner.NEUTRAL
    private val type = selected.type.decodeOrNull() ?: WvwObjectiveType.GENERIC
    private val name = selected.name.translated()

    val title: StringDesc = AppResources.strings.selected_objective.format(name.desc(), owner.stringDesc(), type.stringDesc())
    val subtitle: StringDesc? = matchObjective?.lastFlippedAt?.let { lastFlippedAt ->
        configuration.wvw.flippedAt(lastFlippedAt)
    }
}