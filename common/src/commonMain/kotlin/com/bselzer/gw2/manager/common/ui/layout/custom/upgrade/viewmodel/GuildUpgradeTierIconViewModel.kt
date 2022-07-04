package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.configuration.wvw.WvwGuildUpgradeTier
import com.bselzer.gw2.manager.common.dependency.LocalTheme
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.UpgradeTierIcon
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.datetime.format.minuteFormat
import com.bselzer.ktx.datetime.timer.countdown
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.asImageUrl
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

class GuildUpgradeTierIconViewModel(
    context: AppComponentContext,
    private val tier: WvwGuildUpgradeTier,
    private val startTime: Instant?
) : ViewModel(context), UpgradeTierIcon {
    private val initialAlpha = configuration.alpha(condition = startTime != null)
    private val _alpha = MutableStateFlow(initialAlpha)

    private val countdown: Flow<Duration> = Clock.System.countdown(
        startTime = startTime ?: Instant.DISTANT_PAST,

        // If there is no start time then there must be no claim so the tier will be locked indefinitely.
        duration = if (startTime == null) Duration.INFINITE else tier.hold
    ).onStart {
        _alpha.value = initialAlpha
    }.onCompletion {
        _alpha.value = DefaultAlpha
    }

    override val link: ImageDesc = tier.iconLink.asImageUrl()

    /**
     * Indicates no claim when there is no time. Otherwise, indicates how much time is remaining or how much holding time was required.
     */
    override val description: Flow<StringDesc> = countdown.map { remaining -> remaining.stringDesc() }

    /**
     * Reduces the transparency of the icon until the timer is complete.
     */
    override val alpha: Flow<Float> = _alpha

    /**
     * The tier image is completely white so it is converted to black for light mode.
     */
    override val color: Color?
        @Composable
        get() = when (LocalTheme.current) {
            Theme.LIGHT -> Color.Black
            else -> null
        }

    private fun Duration.stringDesc(): StringDesc = when {
        // If there is no time, then there must be no claim.
        startTime == null -> AppResources.strings.no_claim.desc()

        // Note that the holding period starts from the claim time, NOT from the capture time.
        isPositive() -> AppResources.strings.hold_for.format(minuteFormat())
        else -> AppResources.strings.held_for.format(tier.hold.minuteFormat())
    }
}