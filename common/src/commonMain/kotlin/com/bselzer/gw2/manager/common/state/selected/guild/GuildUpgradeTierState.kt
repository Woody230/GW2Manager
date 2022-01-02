package com.bselzer.gw2.manager.common.state.selected.guild

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
    val upgrades: Collection<GuildUpgradeState>
) : ImageStateAdapter() {
    override var alpha: Float = DefaultAlpha

    /**
     * The amount of time until the tier is unlocked.
     * Transparency is reduced until the timer is complete.
     */
    val remaining: Flow<Duration> = countdown(startTime = startTime ?: Instant.DISTANT_PAST, duration = holdingPeriod).onStart {
        alpha = transparency
        upgrades.forEach { upgrade -> upgrade.alpha = transparency }
    }.onCompletion {
        alpha = DefaultAlpha

        // TODO only do full opacity when the upgrade is slotted
        upgrades.forEach { upgrade -> upgrade.alpha = DefaultAlpha }
    }
}