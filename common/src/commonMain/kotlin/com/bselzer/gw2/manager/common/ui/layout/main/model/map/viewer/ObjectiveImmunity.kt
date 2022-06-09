package com.bselzer.gw2.manager.common.ui.layout.main.model.map.viewer

import com.bselzer.ktx.datetime.timer.countdown
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

data class ObjectiveImmunity(
    /**
     * The amount of time the immunity lasts.
     */
    val duration: Duration?,

    /**
     * The time the immunity starts.
     */
    val startTime: Instant?
) {
    /**
     * The amount of immunity time left.
     */
    val remaining: Flow<Duration> = Clock.System.countdown(startTime = startTime ?: Instant.DISTANT_PAST, duration = duration ?: Duration.ZERO)
}