package com.bselzer.gw2.manager.common.ui.layout.custom.indicator.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.Content
import com.bselzer.gw2.manager.common.ui.layout.custom.indicator.viewmodel.DetailedIconViewModel

class DetailedIconComposition(
    model: DetailedIconViewModel,
    private val onLongClick: () -> Unit,
    private val onClick: () -> Unit
) : ModelComposition<DetailedIconViewModel>(model) {
    @Composable
    override fun DetailedIconViewModel.Content(
        modifier: Modifier
    ) = ConstraintLayout(modifier = modifier) {
        val modifiers = DetailedIconModifiers(scope = this)
        Image(modifier = modifiers.icon)
        IndicatorComposition(progression).Content(modifier = modifiers.progression)
        IndicatorComposition(claim).Content(modifier = modifiers.claim)
        IndicatorComposition(waypoint).Content(modifier = modifiers.waypoint)
        ImmunityComposition(immunity).Content(modifier = modifiers.immunity)
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun DetailedIconViewModel.Image(
        modifier: Modifier
    ) = image.Content(
        size = DpSize(32.dp, 32.dp),
        modifier = modifier.combinedClickable(
            onLongClick = onLongClick,
            onClick = onClick,
        )
    )
}