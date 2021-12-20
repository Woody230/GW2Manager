package com.bselzer.gw2.manager.android.ui.activity.wvw.state.selected

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import com.bselzer.gw2.manager.android.ui.activity.wvw.WvwHelper.color
import com.bselzer.gw2.manager.android.ui.activity.wvw.WvwHelper.displayableLinkedWorlds
import com.bselzer.gw2.manager.android.ui.activity.wvw.WvwHelper.objective
import com.bselzer.gw2.manager.android.ui.activity.wvw.WvwHelper.selectedDateFormatted
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.common.ImageState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.selected.overview.MapState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.selected.overview.OverviewState
import com.bselzer.gw2.manager.android.ui.activity.wvw.state.selected.overview.OwnerState
import com.bselzer.gw2.manager.common.configuration.wvw.Wvw
import com.bselzer.gw2.v2.model.enumeration.extension.wvw.mapType
import com.bselzer.gw2.v2.model.enumeration.extension.wvw.owner
import com.bselzer.gw2.v2.model.extension.wvw.*
import com.bselzer.gw2.v2.model.guild.Guild
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.ktx.function.objects.userFriendly
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class WvwSelectedState(
    private val configuration: Wvw,
    private val match: State<WvwMatch?>,
    private val selectedObjective: State<WvwObjective?>,
    private val worlds: State<Collection<World>>,
    private val upgrades: State<Map<Int, WvwUpgrade>>,
    private val guilds: Map<String, Guild>
) {
    /**
     * The state of the objective image.
     */
    val image: State<ImageState?> = derivedStateOf {
        val objective = selectedObjective.value ?: return@derivedStateOf null
        val fromConfig = configuration.objective(objective)
        val fromMatch = match.value.objective(objective)

        val link = objective.iconLink
        ImageState(
            // Use a default link when the icon link doesn't exist. The link won't exist for atypical types such as Spawn/Mercenary.
            link = if (link.isBlank()) fromConfig?.defaultIconLink else link,
            description = objective.name,
            color = configuration.color(fromMatch),

            // TODO objective images are mostly 32x32 and look awful as result of being scaled like this
            width = 64,
            height = 64,
        )
    }

    /**
     * The state of the overview information.
     */
    val overview: State<OverviewState?> = derivedStateOf {
        val objective = selectedObjective.value ?: return@derivedStateOf null
        val match = match.value
        val fromMatch = match.objective(objective)
        val owner = fromMatch?.owner()
        OverviewState(
            name = "${objective.name} (${objective.type})",
            flipped = fromMatch?.lastFlippedAt?.let { lastFlippedAt ->
                "Flipped at ${configuration.selectedDateFormatted(lastFlippedAt)}"
            },
            map = objective.mapType()?.let { mapType ->
                MapState(
                    name = mapType.userFriendly(),
                    color = configuration.objectives.color(mapType.owner())
                )
            },
            owner = owner?.let {
                OwnerState(
                    name = worlds.value.displayableLinkedWorlds(match = match, owner = owner),
                    color = configuration.objectives.color(owner = owner)
                )
            }
        )
    }

    /**
     * The state of the core objective information.
     */
    val data: State<DataState?> = derivedStateOf {
        val objective = selectedObjective.value ?: return@derivedStateOf null
        val fromMatch = match.value.objective(objective) ?: return@derivedStateOf null
        val upgrade = upgrades.value[objective.upgradeId]
        val yaks = fromMatch.yaksDelivered
        DataState(
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
        val fromMatch = match.value.objective(objective) ?: return@derivedStateOf null
        val claimedAt = fromMatch.claimedAt ?: return@derivedStateOf null

        // Note that claimedBy is the id, so it is necessary to look up the name from the guild model.
        val guildId = fromMatch.claimedBy ?: return@derivedStateOf null
        val name = guilds[guildId]?.name
        if (name.isNullOrBlank()) return@derivedStateOf null

        ClaimState(
            claimedAt = "Claimed at ${configuration.selectedDateFormatted(claimedAt)}",
            claimedBy = "Claimed by $name",
            id = guildId,
            size = 256,
            description = "$name Guild Emblem"
        )
    }
}