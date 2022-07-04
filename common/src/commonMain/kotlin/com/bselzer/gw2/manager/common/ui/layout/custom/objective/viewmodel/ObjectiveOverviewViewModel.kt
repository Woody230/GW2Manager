package com.bselzer.gw2.manager.common.ui.layout.custom.objective.viewmodel

import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.claim.viewmodel.ClaimViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.objective.model.Icon
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.asImageUrl

class ObjectiveOverviewViewModel(
    context: AppComponentContext,
    private val objective: WvwObjective,
    private val matchObjective: WvwMapObjective?,
    private val upgrade: WvwUpgrade?
) : ViewModel(context) {
    private val configObjective: com.bselzer.gw2.manager.common.configuration.wvw.WvwObjective?
        get() = configuration.wvw.objective(objective)

    // Use a default link when the icon link doesn't exist. The link won't exist for atypical types such as Spawn/Mercenary.
    private val link: String? = objective.iconLink.value.ifBlank { configObjective?.defaultIconLink }

    val icon: Icon = Icon(
        link = link?.asImageUrl(),
        description = objective.name.translated().desc(),
        color = matchObjective.color(),
    )


    val core: CoreViewModel? = matchObjective?.let { matchObjective ->
        CoreViewModel(
            context = this,
            matchObjective = matchObjective,
            upgrade = upgrade
        )
    }

    val match: CoreMatchViewModel = CoreMatchViewModel(
        context = this,
        objective = objective,
        matchObjective = matchObjective
    )

    val claim: ClaimViewModel = ClaimViewModel(
        context = this,
        objective = matchObjective
    )
}