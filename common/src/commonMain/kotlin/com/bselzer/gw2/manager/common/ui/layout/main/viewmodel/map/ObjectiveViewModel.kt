package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map

import androidx.compose.ui.graphics.DefaultAlpha
import com.arkivanov.essenty.lifecycle.doOnResume
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.configuration.wvw.WvwGuildUpgradeTier
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective.*
import com.bselzer.gw2.v2.emblem.request.EmblemRequestOptions
import com.bselzer.gw2.v2.model.enumeration.WvwObjectiveType
import com.bselzer.gw2.v2.model.enumeration.extension.enumValueOrNull
import com.bselzer.gw2.v2.model.extension.wvw.*
import com.bselzer.gw2.v2.model.guild.Guild
import com.bselzer.gw2.v2.model.guild.GuildId
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgradeId
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.gw2.v2.model.wvw.objective.WvwMapObjectiveId
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.gw2.v2.resource.strings.stringDesc
import com.bselzer.ktx.datetime.format.minuteFormat
import com.bselzer.ktx.datetime.timer.countdown
import com.bselzer.ktx.logging.Logger
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.asImageUrl
import dev.icerock.moko.resources.format
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

class ObjectiveViewModel(
    context: AppComponentContext,
    private val id: WvwMapObjectiveId,
    showDialog: (DialogConfig) -> Unit
) : MapViewModel(context, showDialog) {
    init {
        lifecycle.doOnResume {
            val guildId = guildId
            if (!guildId.isDefault) {
                CoroutineScope(Dispatchers.Default).launch {
                    repositories.guild.updateGuild(guildId)
                }
            }
        }
    }

    override val title: StringDesc = Gw2Resources.strings.objective.desc()

    private val objective: WvwObjective?
        get() = objectives[id]

    private val fromConfig: com.bselzer.gw2.manager.common.configuration.wvw.WvwObjective?
        get() = configuration.wvw.objective(objective)

    private val fromMatch: WvwMapObjective?
        get() = match.objective(objective)

    private val upgrade: WvwUpgrade?
        get() = upgrades[objective?.upgradeId]

    private val guildId: GuildId
        get() = fromMatch?.claimedBy ?: GuildId()

    private val guild: Guild?
        get() = repositories.guild.guilds[guildId]

    val icon: ObjectiveIcon?
        get() = objective?.let { objective ->
            // Use a default link when the icon link doesn't exist. The link won't exist for atypical types such as Spawn/Mercenary.
            val link = objective.iconLink.value.ifBlank { fromConfig?.defaultIconLink }
            ObjectiveIcon(
                link = link?.asImageUrl(),
                description = objective.name.translated().desc(),
                color = configuration.wvw.color(fromMatch),
            )
        }

    val overview: Overview?
        get() = objective?.let { objective ->
            val name = objective.name.translated()
            val type = objective.type.enumValueOrNull() ?: WvwObjectiveType.GENERIC
            Overview(
                name = AppResources.strings.overview_name.format(name, type.stringDesc()),
                flipped = fromMatch?.lastFlippedAt?.let { lastFlippedAt ->
                    configuration.wvw.flippedAt(lastFlippedAt)
                },
                map = objective.mapType.enumValueOrNull()?.let { mapType ->
                    MapInfo(
                        name = mapType.stringDesc(),
                        color = configuration.wvw.color(owner = mapType.owner())
                    )
                },
                owner = fromMatch?.owner?.enumValueOrNull()?.let { owner ->
                    Owner(
                        name = repositories.selectedWorld.displayableLinkedWorlds(owner),
                        color = configuration.wvw.color(owner)
                    )
                }
            )
        }

    val data: CoreData?
        get() = fromMatch?.let { objective ->
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

    /**
     * The guild claiming the objective.
     */
    val claim: Claim?
        get() {
            val claimedAt = fromMatch?.claimedAt ?: return null

            val guild = guild
            if (guild == null) {
                Logger.w("Attempting to find the claim for a missing guild with id $guildId")
                return null
            }

            // Since we need to use the size when making the request, this is an appropriate case of passing the size in the model.
            val size = 256
            val request = clients.emblem.requestEmblem(guildId.value, size = size, EmblemRequestOptions.MAXIMIZE_BACKGROUND_ALPHA)
            val name = guild.name.translated()
            return Claim(
                claimedAt = configuration.wvw.claimedAt(claimedAt),
                claimedBy = AppResources.strings.claimed_by.format(name),
                link = clients.emblem.emblemUrl(request).asImageUrl(),
                description = AppResources.strings.guild_emblem.format(name),
                size = size
            )
        }

    val shouldShowUpgradeTiers: Boolean
        get() = automaticUpgradeTiers.any { tier -> tier.upgrades.isNotEmpty() }

    val automaticUpgradeTiers: Collection<UpgradeTier>
        // Skip level 0 which only exists in the configuration.
        get() = configuration.wvw.objectives.progressions.drop(1).mapIndexedNotNull { index, progression ->
            val tier = upgrade?.tiers?.getOrNull(index)
            if (tier == null) {
                Logger.w("Attempting to get a missing upgrade tier with index $index when there are ${upgrade?.tiers?.size ?: 0} tiers.")
                return@mapIndexedNotNull null
            }

            val yaksDelivered = fromMatch?.yaksDelivered ?: 0
            val yakRatios = upgrade?.yakRatios(yaksDelivered)?.toList() ?: emptyList()
            val progressed = upgrade?.tiers(yaksDelivered) ?: emptyList()

            // If the tier is not unlocked, then reduce opacity.
            val alpha = configuration.alpha(condition = progressed.contains(tier))

            val yakRatio = yakRatios.getOrElse(index) { Pair(0, 0) }
            val tierName = tier.name.translated()
            UpgradeTier(
                icon = TierDescriptor(
                    link = progression.iconLink.asImageUrl(),
                    description = flowOf(AppResources.strings.upgrade_tier_yaks.format(tierName, yakRatio.first, yakRatio.second)),
                    alpha = flowOf(alpha),
                ),

                upgrades = tier.upgrades.map { upgrade ->
                    Upgrade(
                        name = upgrade.name.translated().desc(),
                        link = upgrade.iconLink.value.asImageUrl(),
                        description = upgrade.description.translated().desc(),
                        alpha = flowOf(alpha),
                    )
                }
            )
        }.filter { tier -> tier.upgrades.isNotEmpty() }

    val shouldShowImprovementTiers: Boolean
        get() = improvementTiers.any { tier -> tier.upgrades.isNotEmpty() }

    val improvementTiers: Collection<GuildUpgradeTier>
        get() = configuration.wvw.objectives.guildUpgrades.improvements.model()

    val shouldShowTacticTiers: Boolean
        get() = tacticTiers.any { tier -> tier.upgrades.isNotEmpty() }

    val tacticTiers: Collection<GuildUpgradeTier>
        get() = configuration.wvw.objectives.guildUpgrades.tactics.model()

    private fun List<WvwGuildUpgradeTier>.model(): Collection<GuildUpgradeTier> {
        val objective = objective ?: return emptyList()
        return map { tier ->
            val startTime = fromMatch?.claimedAt
            val holdingPeriod = tier.holdingPeriod
            val initialAlpha = configuration.alpha(condition = startTime != null)
            val alpha = MutableStateFlow(initialAlpha)

            // Transparency is reduced until the timer is complete.
            val countdown = Clock.System.countdown(
                startTime = startTime ?: Instant.DISTANT_PAST,

                // If there is no start time then there must be no claim so the tier will be locked indefinitely.
                duration = if (startTime == null) Duration.INFINITE else holdingPeriod
            ).onStart {
                alpha.value = initialAlpha
            }.onCompletion {
                alpha.value = DefaultAlpha
            }

            GuildUpgradeTier(
                icon = TierDescriptor(
                    link = tier.iconLink.asImageUrl(),
                    alpha = alpha,
                    description = countdown.map { remaining ->
                        if (startTime == null) {
                            // If there is no time, then there must be no claim.
                            AppResources.strings.no_claim.desc()
                        } else {
                            // Note that the holding period starts from the claim time, NOT from the capture time.
                            // If there is remaining time then display it, otherwise display the amount of time that was needed for this tier to unlock.
                            val holdFor = AppResources.strings.hold_for.format(remaining.minuteFormat())
                            val heldFor = AppResources.strings.held_for.format(tier.holdingPeriod.minuteFormat())
                            if (remaining.isPositive()) holdFor else heldFor
                        }
                    },
                ),

                upgrades = tier.upgrades.filter { upgrade -> upgrade.objectiveTypes.contains(objective.type.enumValueOrNull()) }.mapNotNull { upgrade ->
                    val guildUpgradeId = GuildUpgradeId(upgrade.id)
                    val guildUpgrade = guildUpgrades[guildUpgradeId]
                    if (guildUpgrade == null) {
                        Logger.w("Attempting to determine the state of a missing guild upgrade with id ${upgrade.id}")
                        return@mapNotNull null
                    }

                    Upgrade(
                        name = guildUpgrade.name.translated().desc(),
                        link = guildUpgrade.iconLink.value.asImageUrl(),
                        description = guildUpgrade.description.translated().desc(),

                        // If the upgrade is slotted then provide full opacity.
                        alpha = flowOf(configuration.alpha(condition = fromMatch?.guildUpgradeIds?.contains(guildUpgradeId) == true))
                    )
                }
            )
        }.filter { tier -> tier.upgrades.isNotEmpty() }
    }
}