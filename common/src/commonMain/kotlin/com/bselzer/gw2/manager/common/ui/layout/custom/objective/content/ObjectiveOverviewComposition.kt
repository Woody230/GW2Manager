package com.bselzer.gw2.manager.common.ui.layout.custom.objective.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.*
import com.bselzer.gw2.manager.common.ui.layout.custom.claim.content.ClaimComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.claim.viewmodel.ClaimViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.objective.model.Icon
import com.bselzer.gw2.manager.common.ui.layout.custom.objective.viewmodel.CoreMatchViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.objective.viewmodel.CoreViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.objective.viewmodel.ObjectiveOverviewViewModel
import com.bselzer.ktx.function.collection.buildArray

class ObjectiveOverviewComposition(
    model: ObjectiveOverviewViewModel
) : ModelComposition<ObjectiveOverviewViewModel>(model) {
    @Composable
    override fun ObjectiveOverviewViewModel.Content(
        modifier: Modifier
    ) = InfoSpacedColumn.Projection(
        modifier = modifier,
        content = buildArray {
            // TODO chat link?

            // TODO objective images are mostly 32x32 and look awful as result of being scaled like this
            add { icon.Image() }

            add { match.Content() }

            core?.let { core ->
                add { core.Content() }
            }

            if (claim.exists) {
                add { claim.Content() }
            }
        }
    )

    @Composable
    private fun Icon.Image() = AsyncImage(
        image = link,
        description = description,
        size = DpSize(50.dp, 50.dp),
        color = color
    ).Content(progressIndication = ProgressIndication.ENABLED)

    @Composable
    private fun CoreViewModel.Content() = InfoCard {
        CoreComposition(this@Content).Content()
    }

    @Composable
    private fun CoreMatchViewModel.Content() = InfoCard {
        CoreMatchComposition(this@Content).Content()
    }

    @Composable
    private fun ClaimViewModel.Content() = InfoCard {
        ClaimComposition(this@Content).Content()
    }
}