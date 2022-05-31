package com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlin.time.Duration

data class GuildUpgradeTier(
    val holdingPeriod: Duration,
    val startTime: Instant?,
    val icon: Icon,
    val upgrades: Collection<Upgrade>,

    /**
     * The amount of time until the tier is unlocked.
     */
    val remaining: Flow<Duration>
)