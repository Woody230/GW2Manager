package com.bselzer.gw2.manager.common.ui.layout.custom.indicator.content

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.indicator.viewmodel.ImmunityViewModel
import com.bselzer.ktx.datetime.format.minuteFormat
import kotlin.time.Duration

class ImmunityComposition(
    model: ImmunityViewModel
) : ModelComposition<ImmunityViewModel>(model) {
    @Composable
    override fun ImmunityViewModel.Content(modifier: Modifier) {
        // If the time has finished or the current time is incorrectly set and thus causing an inflated remaining time, do not display it.
        // For the latter case, while the timers shown will be incorrect they will at the very least not be inflated.
        val remaining = remaining.collectAsState(initial = Duration.ZERO).value
        if (!remaining.isPositive() || duration == null || remaining > duration) {
            return
        }

        Text(
            text = remaining.minuteFormat(),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.White,
            modifier = modifier.wrapContentSize()
        )
    }
}