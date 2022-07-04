package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map

import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.custom.objective.viewmodel.ObjectiveOverviewViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.UpgradeTiers
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.viewmodel.AutomaticUpgradeTiersViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.viewmodel.GuildUpgradeTiersViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective.ObjectiveTabType
import com.bselzer.gw2.v2.model.extension.wvw.objective
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.gw2.v2.model.wvw.objective.WvwMapObjectiveId
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.gw2.v2.resource.Gw2Resources
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class ObjectiveViewModel(
    context: AppComponentContext,
    private val id: WvwMapObjectiveId,
    showDialog: (DialogConfig) -> Unit
) : MapViewModel(context, showDialog) {
    override val title: StringDesc = Gw2Resources.strings.objective.desc()

    private val objective: WvwObjective
        get() = objectives[id] ?: WvwObjective()

    private val matchObjective: WvwMapObjective?
        get() = match.objective(objective)

    private val upgrade: WvwUpgrade?
        get() = upgrades[objective.upgradeId]

    val currentTabs: List<ObjectiveTabType>
        get() = buildList {
            add(ObjectiveTabType.DETAILS)

            // Only add the remaining tabs if they have been enabled and their applicable data is available.
            if (automaticUpgradeTiers.shouldShowTiers) {
                add(ObjectiveTabType.AUTOMATIC_UPGRADES)
            }

            if (improvementTiers.shouldShowTiers) {
                add(ObjectiveTabType.GUILD_IMPROVEMENTS)
            }

            if (tacticTiers.shouldShowTiers) {
                add(ObjectiveTabType.GUILD_TACTICS)
            }
        }

    val overview: ObjectiveOverviewViewModel
        get() = ObjectiveOverviewViewModel(
            context = this,
            objective = objective,
            matchObjective = matchObjective,
            upgrade = upgrade
        )

    val automaticUpgradeTiers: UpgradeTiers
        get() = AutomaticUpgradeTiersViewModel(
            context = this,
            upgrade = upgrade ?: WvwUpgrade(),
            yaksDelivered = matchObjective?.yaksDelivered ?: 0
        )

    val improvementTiers: UpgradeTiers
        get() = GuildUpgradeTiersViewModel(
            context = this,
            tiers = configuration.wvw.objectives.guildUpgrades.improvements,
            objective = matchObjective
        )

    val tacticTiers: UpgradeTiers
        get() = GuildUpgradeTiersViewModel(
            context = this,
            tiers = configuration.wvw.objectives.guildUpgrades.tactics,
            objective = matchObjective
        )
}