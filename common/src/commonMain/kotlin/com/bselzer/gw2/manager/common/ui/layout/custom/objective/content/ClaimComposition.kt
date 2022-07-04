package com.bselzer.gw2.manager.common.ui.layout.custom.objective.content

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.objective.model.Claim
import com.bselzer.gw2.manager.common.ui.layout.custom.objective.viewmodel.ClaimViewModel
import com.bselzer.ktx.compose.resource.strings.localized

/**
 * Lays out the guild claiming the objective.
 */
class ClaimComposition(
    model: ClaimViewModel
) : ViewModelComposition<ClaimViewModel>(model) {
    @Composable
    override fun ClaimViewModel.Content(modifier: Modifier) {
        claim?.Content(modifier)
    }

    @Composable
    private fun Claim.Content(modifier: Modifier) = Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        ClaimedAt()
        ClaimedBy()
        Icon()
    }

    @Composable
    private fun Claim.ClaimedAt() = Text(text = claimedAt.localized(), textAlign = TextAlign.Center)

    @Composable
    private fun Claim.ClaimedBy() = Text(text = claimedBy.localized(), textAlign = TextAlign.Center)

    @Composable
    private fun Claim.Icon() = ClaimIndicatorComposition(icon).Content()
}