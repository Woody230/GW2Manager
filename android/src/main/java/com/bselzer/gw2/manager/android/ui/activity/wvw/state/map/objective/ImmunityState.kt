package com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.objective

import androidx.compose.ui.unit.TextUnit
import kotlinx.datetime.Clock
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

    /**
     * The amount of milliseconds to delay by before attempting to update the remaining time.
     */
    val delay: Long
) {
    /**
     * The amount of immunity time left.
     */
    val remaining: Duration
        get() = if (duration == null || startTime == null) {
            Duration.seconds(0)
        } else {
            duration - Clock.System.now().minus(startTime)
        }

    /**
     * The amount of immunity time left formatted into a user friendly string.
     *
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html">formatting</a>
     */
    val formattedRemaining: String
        get() {
            val totalSeconds = remaining.inWholeSeconds
            val seconds: Int = (totalSeconds % 60).toInt()
            val minutes: Int = (totalSeconds / 60).toInt()
            return "%01d:%02d".format(minutes, seconds)
        }
}