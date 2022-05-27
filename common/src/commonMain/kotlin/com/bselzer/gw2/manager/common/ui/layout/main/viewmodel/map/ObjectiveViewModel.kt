package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map

import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.v2.model.wvw.objective.WvwMapObjectiveId
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class ObjectiveViewModel(
    context: AppComponentContext,
    id: WvwMapObjectiveId,
    showDialog: (DialogConfig) -> Unit
) : MapViewModel(context, showDialog) {
    override val title: StringDesc = Gw2Resources.strings.objective.desc()

    // TODO on init refresh guild information based on claimedBy in match
}