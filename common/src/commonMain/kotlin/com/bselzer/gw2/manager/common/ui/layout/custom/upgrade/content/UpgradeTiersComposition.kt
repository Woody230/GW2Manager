package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.BorderedCard
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.UpgradeTier
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.UpgradeTiers
import com.bselzer.ktx.compose.ui.layout.column.ColumnPresenter
import com.bselzer.ktx.compose.ui.layout.column.spacedColumnProjector
import com.bselzer.ktx.compose.ui.layout.merge.TriState
import com.bselzer.ktx.function.collection.buildArray

class UpgradeTiersComposition(
    model: UpgradeTiers
) : ModelComposition<UpgradeTiers>(model) {
    @Composable
    override fun UpgradeTiers.Content(
        modifier: Modifier
    ) = spacedColumnProjector(
        thickness = 10.dp,
        presenter = ColumnPresenter(
            prepend = TriState.TRUE,
            append = TriState.TRUE,
            horizontalAlignment = Alignment.CenterHorizontally
        )
    ).Projection(
        modifier = modifier,
        content = buildArray {
            tiers.forEach { tier ->
                add { tier.Content() }
            }
        }
    )

    @Composable
    private fun UpgradeTier.Content() {
        val isExpanded = remember { mutableStateOf(false) }
        BorderedCard(
            modifier = Modifier
                .fillMaxWidth(.90f)
                .clickable { isExpanded.value = !isExpanded.value }
        ) {
            UpgradeTierComposition(
                model = this@Content,
                isExpanded = isExpanded.value
            ).Content(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 5.dp)
            )
        }
    }
}