package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map

import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.custom.claim.viewmodel.ClaimViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.UpgradeTiers
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.viewmodel.AutomaticUpgradeTiersViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.viewmodel.GuildUpgradeTiersViewModel
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective.*
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveType
import com.bselzer.gw2.v2.model.enumeration.extension.decodeOrNull
import com.bselzer.gw2.v2.model.extension.wvw.*
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.gw2.v2.model.wvw.objective.WvwMapObjectiveId
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.gw2.v2.resource.strings.stringDesc
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.asImageUrl
import dev.icerock.moko.resources.format

class ObjectiveViewModel(
    context: AppComponentContext,
    private val id: WvwMapObjectiveId,
    showDialog: (DialogConfig) -> Unit
) : MapViewModel(context, showDialog) {
    override val title: StringDesc = Gw2Resources.strings.objective.desc()

    private val objective: WvwObjective?
        get() = objectives[id]

    private val fromConfig: com.bselzer.gw2.manager.common.configuration.wvw.WvwObjective?
        get() = configuration.wvw.objective(objective)

    private val matchObjective: WvwMapObjective?
        get() = match.objective(objective)

    private val upgrade: WvwUpgrade?
        get() = upgrades[objective?.upgradeId]

    val icon: ObjectiveIcon?
        get() = objective?.let { objective ->
            // Use a default link when the icon link doesn't exist. The link won't exist for atypical types such as Spawn/Mercenary.
            val link = objective.iconLink.value.ifBlank { fromConfig?.defaultIconLink }
            ObjectiveIcon(
                link = link?.asImageUrl(),
                description = objective.name.translated().desc(),
                color = matchObjective.color(),
            )
        }

    val overview: Overview?
        get() = objective?.let { objective ->
            val name = objective.name.translated()
            val type = objective.type.decodeOrNull() ?: WvwObjectiveType.GENERIC
            Overview(
                name = AppResources.strings.overview_name.format(name, type.stringDesc()),
                flipped = matchObjective?.lastFlippedAt?.let { lastFlippedAt ->
                    configuration.wvw.flippedAt(lastFlippedAt)
                },
                map = objective.mapType.decodeOrNull()?.let { mapType ->
                    MapInfo(
                        name = mapType.stringDesc(),
                        color = mapType.owner().color()
                    )
                },
                owner = matchObjective?.owner?.decodeOrNull()?.let { owner ->
                    Owner(
                        name = repositories.selectedWorld.displayableLinkedWorlds(owner),
                        color = owner.color()
                    )
                }
            )
        }

    val data: CoreData?
        get() = matchObjective?.let { objective ->
            val yaksDelivered = objective.yaksDelivered
            CoreData(
                pointsPerTick = Gw2Resources.strings.points_per_tick.desc() to objective.pointsPerTick.toString().desc(),
                pointsPerCapture = Gw2Resources.strings.points_per_capture.desc() to objective.pointsPerCapture.toString().desc(),
                yaks = upgrade?.let { upgrade ->
                    val ratio = upgrade.yakRatio(yaksDelivered)
                    AppResources.strings.yaks_delivered.desc() to AppResources.strings.yaks_delivered_ratio.format(ratio.first, ratio.second)
                },
                upgrade = upgrade?.let { upgrade ->
                    val level = upgrade.level(yaksDelivered)

                    val tier = upgrade.tier(yaksDelivered)?.name?.translated() ?: AppResources.strings.no_upgrade.desc()
                    AppResources.strings.upgrade_tier.desc() to AppResources.strings.upgrade_tier_level.format(tier, level, upgrade.tiers.size)
                }
            )
        }

    val claim: ClaimViewModel
        get() = ClaimViewModel(context = this, objective = matchObjective)

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