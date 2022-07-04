package com.bselzer.gw2.manager.common.ui.layout.custom.indicator.viewmodel

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.repository.data.generic.GuildData
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.common.ImageAdapter
import com.bselzer.gw2.v2.model.extension.wvw.tiers
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgrade
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.gw2.v2.model.wvw.upgrade.tier.WvwTierUpgrade
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.asImageUrl

class WaypointIndicatorViewModel(
    context: AppComponentContext,
    matchObjective: WvwMapObjective,
    upgrade: WvwUpgrade?
) : ViewModel(context), GuildData by context.repositories.guild, ImageAdapter {
    /**
     * The upgrades associated with progressed tiers.
     */
    private val tiers: List<WvwTierUpgrade> = upgrade?.tiers(matchObjective.yaksDelivered)?.flatMap { tier -> tier.upgrades } ?: emptyList()

    /**
     * Whether the permanent waypoint exists via the [WvwUpgrade].
     */
    private val hasWaypointUpgrade: Boolean = tiers.any { tier -> configuration.wvw.objectives.waypoint.upgradeName.matches(tier.name) }

    /**
     * Whether the temporary waypoint exists via the [GuildUpgrade]s.
     */
    private val hasWaypointTactic: Boolean = run {
        val guildUpgrades = matchObjective.guildUpgradeIds.mapNotNull { id -> guildUpgrades[id] }
        guildUpgrades.any { tactic -> configuration.wvw.objectives.waypoint.guild.upgradeName.matches(tactic.name) }
    }

    override val enabled: Boolean = hasWaypointUpgrade || hasWaypointTactic
    override val image: ImageDesc? = configuration.wvw.objectives.waypoint.iconLink?.asImageUrl()

    override val description: StringDesc? = when {
        hasWaypointUpgrade -> AppResources.strings.permanent_waypoint.desc()
        hasWaypointTactic -> AppResources.strings.temporary_waypoint.desc()
        else -> null
    }

    override val color: Color? = when {
        hasWaypointTactic && !hasWaypointUpgrade -> configuration.wvw.objectives.waypoint.guild.color
        else -> null
    }
}