package com.bselzer.gw2.manager.common.state.selected.upgrade

import androidx.compose.ui.graphics.DefaultAlpha
import com.bselzer.gw2.manager.common.ui.composable.ImageStateAdapter
import com.bselzer.ktx.datetime.timer.countdown
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Instant
import kotlin.time.Duration

data class GuildUpgradeTierState(
    val holdingPeriod: Duration,
    val startTime: Instant?,
    override val link: String?,
    override val height: Int,
    override val width: Int,
    val transparency: Float,
    val upgrades: Collection<UpgradeState>
) : ImageStateAdapter() {
    override var alpha: Float = if (startTime != null) DefaultAlpha else transparency

    /**
     * The amount of time until the tier is unlocked.
     * Transparency is reduced until the timer is complete.
     */
    val remaining: Flow<Duration> = countdown(
        startTime = startTime ?: Instant.DISTANT_PAST,

        // If there is no start time then there must be no claim so the tier will be locked indefinitely.
        duration = if (startTime == null) Duration.INFINITE else holdingPeriod
    ).onStart { alpha = transparency }.onCompletion { alpha = DefaultAlpha }
}