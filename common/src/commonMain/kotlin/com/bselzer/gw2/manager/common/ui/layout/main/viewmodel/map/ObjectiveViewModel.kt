package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.map

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.DefaultAlpha
import com.arkivanov.essenty.lifecycle.doOnResume
import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.configuration.WvwHelper.color
import com.bselzer.gw2.manager.common.configuration.WvwHelper.displayableLinkedWorlds
import com.bselzer.gw2.manager.common.configuration.WvwHelper.objective
import com.bselzer.gw2.manager.common.configuration.WvwHelper.selectedDateFormatted
import com.bselzer.gw2.manager.common.configuration.wvw.WvwGuildUpgradeTier
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.dialog.configuration.DialogConfig
import com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective.*
import com.bselzer.gw2.v2.emblem.request.EmblemRequestOptions
import com.bselzer.gw2.v2.model.enumeration.extension.enumValueOrNull
import com.bselzer.gw2.v2.model.extension.wvw.*
import com.bselzer.gw2.v2.model.guild.Guild
import com.bselzer.gw2.v2.model.guild.GuildId
import com.bselzer.gw2.v2.model.guild.upgrade.GuildUpgradeId
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.gw2.v2.model.wvw.objective.WvwMapObjectiveId
import com.bselzer.gw2.v2.model.wvw.objective.WvwObjective
import com.bselzer.gw2.v2.model.wvw.upgrade.WvwUpgrade
import com.bselzer.ktx.datetime.timer.countdown
import com.bselzer.ktx.function.objects.userFriendly
import com.bselzer.ktx.logging.Logger
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.asImageUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
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

    // TODO on init refresh guild information based on claimedBy in match

    val icon: Icon?
        get() = objective?.let { objective ->
            // Use a default link when the icon link doesn't exist. The link won't exist for atypical types such as Spawn/Mercenary.
            val link = objective.iconLink.value.ifBlank { fromConfig?.defaultIconLink }
            Icon(
                link = link?.asImageUrl(),
                description = objective.name.desc(),
                color = configuration.wvw.color(fromMatch),

                // TODO use directly in composition
                // TODO objective images are mostly 32x32 and look awful as result of being scaled like this
                width = 128,
                height = 128
            )
        }

    val overview: Overview?
        get() = objective?.let { objective ->
            Overview(
                // TODO translated
                name = "${objective.name} (${objective.type})".desc(),
                flipped = fromMatch?.lastFlippedAt?.let { lastFlippedAt ->
                    // TODO translated
                    "Flipped at ${configuration.wvw.selectedDateFormatted(lastFlippedAt)}".desc()
                },
                map = objective.mapType.enumValueOrNull()?.let { mapType ->
                    MapInfo(
                        // TODO translated
                        name = mapType.userFriendly().desc(),
                        color = configuration.wvw.color(owner = mapType.owner())
                    )
                },
                owner = fromMatch?.owner?.enumValueOrNull()?.let { owner ->
                    Owner(
                        // TODO translated
                        name = repositories.world.worlds.values.displayableLinkedWorlds(match, owner).desc(),
                        color = configuration.wvw.color(owner)
                    )
                }
            )
        }

    val data: CoreData?
        get() = fromMatch?.let { objective ->
            val yaksDelivered = objective.yaksDelivered
            CoreData(
                // TODO translations
                pointsPerTick = "Points per tick:".desc() to objective.pointsPerTick.toString().desc(),
                pointsPerCapture = "Points per capture".desc() to objective.pointsPerCapture.toString().desc(),
                yaks = upgrade?.let { upgrade ->
                    val ratio = upgrade.yakRatio(yaksDelivered)
                    "Yaks delivered:".desc() to "${ratio.first}/${ratio.second}".desc()
                },
                upgrade = upgrade?.let { upgrade ->
                    val level = upgrade.level(yaksDelivered)
                    val tier = upgrade.tier(yaksDelivered)?.name ?: "Not Upgraded"
                    "Upgrade tier:".desc() to "$tier ($level/${upgrade.tiers.size})".desc()
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

            val size = 256
            val request = clients.emblem.requestEmblem(guildId.value, size = size, EmblemRequestOptions.MAXIMIZE_BACKGROUND_ALPHA)
            val name = guild.name
            return Claim(
                // TODO translations
                claimedAt = "Claimed at ${configuration.wvw.selectedDateFormatted(claimedAt)}".desc(),
                claimedBy = "Claimed by $name".desc(),
                icon = Icon(
                    link = clients.emblem.emblemUrl(request).asImageUrl(),
                    width = size,
                    height = size,
                    description = "$name Guild Emblem".desc(),
                    color = null,
                )
            )
        }

    val shouldShowUpgradeTiers: Boolean
        get() = configuration.wvw.objectives.progressions.enabled && automaticUpgradeTiers.any { tier -> tier.upgrades.isNotEmpty() }

    val automaticUpgradeTiers: Collection<UpgradeTier>
        // Skip level 0 which only exists in the configuration.
        get() = configuration.wvw.objectives.progressions.progression.drop(1).mapIndexedNotNull { index, progression ->
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
            UpgradeTier(
                icon = Icon(
                    link = progression.iconLink.asImageUrl(),

                    // TODO remove from config
                    width = configuration.wvw.objectives.progressions.tierIconSize.width,
                    height = configuration.wvw.objectives.progressions.tierIconSize.height,

                    // TODO translated
                    description = "${tier.name} (${yakRatio.first}/${yakRatio.second})".desc(),
                    alpha = alpha,
                    color = null,
                ),

                upgrades = tier.upgrades.map { upgrade ->
                    Upgrade(
                        // TODO translated
                        name = upgrade.name.desc(),

                        icon = Icon(
                            link = upgrade.iconLink.value.asImageUrl(),
                            description = upgrade.description.desc(),
                            alpha = alpha,
                            color = null,

                            // TODO remove from config
                            width = configuration.wvw.objectives.progressions.iconSize.width,
                            height = configuration.wvw.objectives.progressions.iconSize.height,
                        )
                    )
                }
            )
        }.filter { tier -> tier.upgrades.isNotEmpty() }

    val shouldShowImprovementTiers: Boolean
        get() = configuration.wvw.objectives.guildUpgrades.enabled && improvementTiers.any { tier -> tier.upgrades.isNotEmpty() }

    val improvementTiers: Collection<GuildUpgradeTier>
        get() = configuration.wvw.objectives.guildUpgrades.improvements.model()

    val shouldShowTacticTiers: Boolean
        get() = configuration.wvw.objectives.guildUpgrades.enabled && tacticTiers.any { tier -> tier.upgrades.isNotEmpty() }

    val tacticTiers: Collection<GuildUpgradeTier>
        get() = configuration.wvw.objectives.guildUpgrades.tactics.model()

    private fun List<WvwGuildUpgradeTier>.model(): Collection<GuildUpgradeTier> {
        val objective = objective ?: return emptyList()
        return map { tier ->
            val startTime = fromMatch?.claimedAt
            val holdingPeriod = tier.holdingPeriod
            val initialAlpha = configuration.alpha(condition = startTime == null)
            val alpha = mutableStateOf(initialAlpha)
            GuildUpgradeTier(
                // Note that the holding period starts from the claim time, NOT from the capture time.
                startTime = startTime,
                holdingPeriod = holdingPeriod,

                icon = Icon(
                    link = tier.iconLink.asImageUrl(),

                    // TODO remove from config
                    width = configuration.wvw.objectives.guildUpgrades.tierSize.width,
                    height = configuration.wvw.objectives.guildUpgrades.tierSize.height,

                    description = "".desc(),
                    color = null,
                    alpha = alpha.value,
                ),

                // Transparency is reduced until the timer is complete.
                remaining = Clock.System.countdown(
                    startTime = startTime ?: Instant.DISTANT_PAST,

                    // If there is no start time then there must be no claim so the tier will be locked indefinitely.
                    duration = if (startTime == null) Duration.INFINITE else holdingPeriod
                ).onStart {
                    alpha.value = initialAlpha
                }.onCompletion {
                    alpha.value = DefaultAlpha
                },

                upgrades = tier.upgrades.filter { upgrade -> upgrade.objectiveTypes.contains(objective.type.enumValueOrNull()) }.mapNotNull { upgrade ->
                    val guildUpgradeId = GuildUpgradeId(upgrade.id)
                    val guildUpgrade = guildUpgrades[guildUpgradeId]
                    if (guildUpgrade == null) {
                        Logger.w("Attempting to determine the state of a missing guild upgrade with id ${upgrade.id}")
                        return@mapNotNull null
                    }

                    Upgrade(
                        icon = Icon(
                            link = guildUpgrade.iconLink.value.asImageUrl(),
                            description = guildUpgrade.description.desc(),
                            color = null,

                            // TODO remove from config
                            width = configuration.wvw.objectives.guildUpgrades.size.width,
                            height = configuration.wvw.objectives.guildUpgrades.size.height,

                            // If the upgrade is slotted then provide full opacity.
                            alpha = configuration.alpha(condition = fromMatch?.guildUpgradeIds?.contains(guildUpgradeId) == true)
                        ),

                        // TODO translations
                        name = guildUpgrade.name.desc(),
                    )
                }
            )
        }.filter { tier -> tier.upgrades.isNotEmpty() }
    }
}