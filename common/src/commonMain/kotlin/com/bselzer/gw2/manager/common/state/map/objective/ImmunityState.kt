package com.bselzer.gw2.manager.common.state.map.objective

import androidx.compose.ui.unit.TextUnit
import com.bselzer.ktx.datetime.timer.countdown
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class ImmunityState(
    val enabled: Boolean,
    val textSize: TextUnit,

    /**
     * The amount of time the immunity lasts.
     */
    val duration: Duration?,

    /**
     * The time the immunity starts.
     */
    val startTime: Instant?,
) {
    /**
     * The amount of immunity time left.
     */
    val remaining: Flow<Duration> = countdown(startTime = startTime ?: Instant.DISTANT_PAST, duration = duration ?: Duration.ZERO)
}