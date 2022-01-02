package com.bselzer.gw2.manager.common.state.selected

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.graphics.DefaultAlpha
import com.bselzer.gw2.manager.common.configuration.wvw.WvwGuildUpgradeTier
import com.bselzer.gw2.manager.common.state.WvwHelper.color
import com.bselzer.gw2.manager.common.state.WvwHelper.displayableLinkedWorlds
import com.bselzer.gw2.manager.common.state.WvwHelper.objective
import com.bselzer.gw2.manager.common.state.WvwHelper.selectedDateFormatted
import com.bselzer.gw2.manager.common.state.core.Gw2State
import com.bselzer.gw2.manager.common.state.selected.overview.MapState
import com.bselzer.gw2.manager.common.state.selected.overview.OverviewState
import com.bselzer.gw2.manager.common.state.selected.overview.OwnerState
import com.bselzer.gw2.manager.common.state.selected.upgrade.GuildUpgradeTierState
import com.bselzer.gw2.manager.common.state.selected.upgrade.UpgradeState
import com.bselzer.gw2.manager.common.state.selected.upgrade.UpgradeTierState
import com.bselzer.gw2.manager.common.ui.composable.ImageState
import com.bselzer.gw2.v2.emblem.request.EmblemRequestOptions
import com.bselzer.gw2.v2.model.enumeration.extension.wvw.mapType
import com.bselzer.gw2.v2.model.enumeration.extension.wvw.owner
import com.bselzer.gw2.v2.model.enumeration.extension.wvw.type
import com.bselzer.gw2.v2.model.extension.wvw.*
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.ktx.function.objects.userFriendly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class WvwSelectedState(
    state: Gw2State,
    val selectedObjective: State<WvwObjective?>
) : Gw2State by state {
    /**
     * The state of the objective image.
     */
    val image: State<ImageState?> = derivedStateOf {
        val objective = selectedObjective.value ?: return@derivedStateOf null
        val fromConfig = configuration.wvw.objective(objective)
        val fromMatch = worldMatch.value.objective(objective)

        val link = objective.iconLink
        ObjectiveImageState(
            // Use a default link when the icon link doesn't exist. The link won't exist for atypical types such as Spawn/Mercenary.
            link = if (link.isBlank()) fromConfig?.defaultIconLink else link,
            description = objective.name,
            color = configuration.wvw.color(fromMatch),

            // TODO objective images are mostly 32x32 and look awful as result of being scaled like this
            width = 128,
            height = 128
        )
    }

    /**
     * The state of the overview information.
     */
    val overview: State<OverviewState?> = derivedStateOf {
        val objective = selectedObjective.value ?: return@derivedStateOf null
        val match = worldMatch.value
        val fromMatch = match.objective(objective)
        val owner = fromMatch?.owner()
        OverviewState(
            name = "${objective.name} (${objective.type})",
            flipped = fromMatch?.lastFlippedAt?.let { lastFlippedAt ->
                "Flipped at ${configuration.wvw.selectedDateFormatted(lastFlippedAt)}"
            },
            map = objective.mapType()?.let { mapType ->
                MapState(
                    name = mapType.userFriendly(),
                    color = configuration.wvw.color(owner = mapType.owner())
                )
            },
            owner = owner?.let {
                OwnerState(
                    name = worlds.values.displayableLinkedWorlds(match = match, owner = owner),
                    color = configuration.wvw.color(owner = owner)
                )
            }
        )
    }

    /**
     * The state of the core objective information.
     */
    val data: State<SelectedDataState?> = derivedStateOf {
        val objective = selectedObjective.value ?: return@derivedStateOf null
        val fromMatch = worldMatch.value.objective(objective) ?: return@derivedStateOf null
        val upgrade = upgrades[objective.upgradeId]
        val yaks = fromMatch.yaksDelivered
        SelectedDataState(
            pointsPerTick = "Points per tick:" to fromMatch.pointsPerTick.toString(),
            pointsPerCapture = "Points per capture:" to fromMatch.pointsPerCapture.toString(),
            yaks = upgrade?.let {
                val ratio = upgrade.yakRatio(yaksDelivered = yaks)
                "Yaks delivered:" to "${ratio.first}/${ratio.second}"
            },
            upgrade = upgrade?.let {
                val level = upgrade.level(yaksDelivered = yaks)
                val tier = upgrade.tier(yaksDelivered = yaks)?.name ?: "Not Upgraded"
                "Upgrade tier:" to "$tier ($level/${upgrade.tiers.size})"
            }
        )
    }

    /**
     * The state of the guild claim over the objective.
     */
    val claim: State<ClaimState?> = derivedStateOf {
        val objective = selectedObjective.value ?: return@derivedStateOf null
        val fromMatch = worldMatch.value.objective(objective) ?: return@derivedStateOf null
        val claimedAt = fromMatch.claimedAt ?: return@derivedStateOf null

        // Note that claimedBy is the id, so it is necessary to look up the name from the guild model.
        val guildId = fromMatch.claimedBy ?: return@derivedStateOf null
        val guild = guilds[guildId]
        if (guild == null) {
            // Attempt to reconcile the missing data.
            CoroutineScope(Dispatchers.IO).launch {
                refreshGuild(guildId)
            }
            return@derivedStateOf null
        }

        val name = guild.name
        if (name.isBlank()) return@derivedStateOf null

        val size = 256
        val request = emblemClient.requestEmblem(guildId = guildId, size = size, EmblemRequestOptions.MAXIMIZE_BACKGROUND_ALPHA)
        ClaimState(
            claimedAt = "Claimed at ${configuration.wvw.selectedDateFormatted(claimedAt)}",
            claimedBy = "Claimed by $name",
            link = emblemClient.emblemUrl(request),
            width = size,
            height = size,
            description = "$name Guild Emblem"
        )
    }

    /**
     * Whether the upgrade tiers should be displayed.
     */
    val shouldShowUpgradeTiers = derivedStateOf {
        configuration.wvw.objectives.progressions.enabled && automaticUpgradeTiers.value.flatMap { tier -> tier.upgrades }.isNotEmpty()
    }

    /**
     * The state of the objective upgrades and its tiers.
     */
    val automaticUpgradeTiers: State<Collection<UpgradeTierState>> = derivedStateOf {
        val objective = selectedObjective.value ?: return@derivedStateOf emptyList<UpgradeTierState>()
        val selectedUpgrade = upgrades[objective.upgradeId]
        val fromMatch = worldMatch.value.objective(objective)
        val yaksDelivered = fromMatch?.yaksDelivered ?: 0
        val yakRatios = selectedUpgrade?.yakRatios(yaksDelivered = yaksDelivered)?.toList() ?: emptyList()
        val progressed = selectedUpgrade?.tiers(yaksDelivered = yaksDelivered) ?: emptyList()

        // Skip level 0 which only exists in the configuration.
        configuration.wvw.objectives.progressions.progression.drop(1).mapIndexedNotNull { index, progression ->
            val tier = selectedUpgrade?.tiers?.getOrNull(index) ?: return@mapIndexedNotNull null
            val yakRatio = yakRatios.getOrNull(index) ?: Pair(0, 0)

            // If the tier is not unlocked, then reduce opacity.
            val alpha = configuration.alpha(condition = progressed.contains(tier))
            UpgradeTierState(
                link = progression.iconLink,
                width = configuration.wvw.objectives.progressions.tierIconSize.width,
                height = configuration.wvw.objectives.progressions.tierIconSize.height,
                description = "${tier.name} (${yakRatio.first}/${yakRatio.second})",
                alpha = alpha,

                upgrades = tier.upgrades.map { upgrade ->
                    UpgradeState(
                        name = upgrade.name,
                        link = upgrade.iconLink,
                        description = upgrade.description,
                        width = configuration.wvw.objectives.progressions.iconSize.width,
                        height = configuration.wvw.objectives.progressions.iconSize.height,
                        alpha = alpha
                    )
                }
            )
        }.filter { tier -> tier.upgrades.isNotEmpty() }
    }

    /**
     * Whether the guild upgrade improvement tiers should be displayed.
     */
    val shouldShowImprovementTiers = derivedStateOf {
        configuration.wvw.objectives.guildUpgrades.enabled && improvementTiers.value.flatMap { tier -> tier.upgrades }.isNotEmpty()
    }

    /**
     * The state of the objective improvements.
     */
    val improvementTiers: State<Collection<GuildUpgradeTierState>> = guildUpgradeStates(configuration.wvw.objectives.guildUpgrades.improvements)

    /**
     * Whether the guild upgrade tactic tiers should be displayed.
     */
    val shouldShowTacticTiers = derivedStateOf {
        configuration.wvw.objectives.guildUpgrades.enabled && tacticTiers.value.flatMap { tier -> tier.upgrades }.isNotEmpty()
    }

    /**
     * The state of the objective tactics.
     */
    val tacticTiers: State<Collection<GuildUpgradeTierState>> = guildUpgradeStates(configuration.wvw.objectives.guildUpgrades.tactics)

    /**
     * The state of the objective's guild upgrades.
     */
    private fun guildUpgradeStates(tiers: List<WvwGuildUpgradeTier>): State<Collection<GuildUpgradeTierState>> = derivedStateOf {
        val objective = selectedObjective.value ?: return@derivedStateOf emptyList()
        val fromMatch = worldMatch.value.objective(objective)
        tiers.map { tier ->
            GuildUpgradeTierState(
                holdingPeriod = tier.holdingPeriod,

                // Note that the holding period starts from the claim time, NOT from the capture time.
                startTime = fromMatch?.claimedAt,

                link = tier.iconLink,
                width = configuration.wvw.objectives.guildUpgrades.tierSize.width,
                height = configuration.wvw.objectives.guildUpgrades.tierSize.height,
                transparency = configuration.transparency,

                // Filtering on objective type to make sure the upgrades are suitable for this kind of objective.
                upgrades = tier.upgrades.filter { upgrade -> upgrade.objectiveTypes.contains(objective.type()) }.mapNotNull { upgrade ->
                    // All of the usable guild upgrades need to be configured since the api doesn't provide a list.
                    val guildUpgrade = guildUpgrades[upgrade.id] ?: return@mapNotNull null
                    UpgradeState(
                        link = guildUpgrade.iconLink,
                        width = configuration.wvw.objectives.guildUpgrades.size.width,
                        height = configuration.wvw.objectives.guildUpgrades.size.height,
                        name = guildUpgrade.name,
                        description = guildUpgrade.description,

                        // If the upgrade is slotted then provide full opacity.
                        alpha = if (fromMatch?.guildUpgradeIds?.contains(upgrade.id) == true) DefaultAlpha else configuration.transparency
                    )
                }
            )
        }.filter { tier -> tier.upgrades.isNotEmpty() }
    }
}