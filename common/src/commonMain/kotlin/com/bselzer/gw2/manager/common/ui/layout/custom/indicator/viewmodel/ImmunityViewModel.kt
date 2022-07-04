package com.bselzer.gw2.manager.common.ui.layout.custom.indicator.viewmodel

import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.v2.model.wvw.map.WvwMapObjective
import com.bselzer.ktx.datetime.timer.countdown
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

class ImmunityViewModel(
    context: AppComponentContext,
    matchObjective: WvwMapObjective
) : ViewModel(context) {
    private val configObjective = configuration.wvw.objective(matchObjective)

    /**
     * The amount of time the immunity lasts.
     */
    val duration: Duration? = configObjective?.immunity

    /**
     * The time the immunity starts.
     */
    val startTime: Instant? = matchObjective.lastFlippedAt

    /**
     * The amount of immunity time left.
     */
    val remaining: Flow<Duration> = Clock.System.countdown(startTime = startTime ?: Instant.DISTANT_PAST, duration = duration ?: Duration.ZERO)
}