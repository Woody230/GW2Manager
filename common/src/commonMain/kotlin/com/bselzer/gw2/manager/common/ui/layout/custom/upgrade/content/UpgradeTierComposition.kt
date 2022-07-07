package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.content

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.UpgradeTier
import com.bselzer.ktx.compose.ui.intl.LocalLocale
import com.bselzer.ktx.compose.ui.layout.column.ColumnPresenter
import com.bselzer.ktx.compose.ui.layout.column.spacedColumnProjector
import com.bselzer.ktx.compose.ui.layout.icon.IconProjector
import com.bselzer.ktx.compose.ui.layout.icon.expansionIconInteractor
import com.bselzer.ktx.compose.ui.layout.merge.TriState
import com.bselzer.ktx.function.collection.buildArray
import com.bselzer.ktx.resource.strings.localized
import dev.icerock.moko.resources.desc.desc

class UpgradeTierComposition(
    model: UpgradeTier,
    private val isExpanded: Boolean
) : ModelComposition<UpgradeTier>(model) {
    @Composable
    override fun UpgradeTier.Content(modifier: Modifier) = spacedColumnProjector(
        thickness = 15.dp,
        presenter = ColumnPresenter(prepend = TriState.TRUE, append = TriState.TRUE)
    ).Projection(
        modifier = modifier,
        content = buildArray {
            // TODO (un)lock icon?
            add { Header() }

            // Only show the upgrade content when expanded to save space.
            if (isExpanded) {
                upgrades.forEach { upgrade ->
                    add { UpgradeComposition(upgrade).Content(modifier = Modifier.fillMaxWidth()) }
                }
            }
        }
    )

    @Composable
    private fun UpgradeTier.Header() = ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val modifiers = UpgradeTierModifiers(scope = this)
        UpgradeTierIconComposition(icon).Content(modifier = modifiers.icon)
        Descriptor(modifier = modifiers.descriptor)
        ExpansionIcon(modifier = modifiers.expansion)
    }

    @Composable
    private fun UpgradeTier.Descriptor(modifier: Modifier) {
        val description = icon.description.collectAsState("".desc()).value.localized()
        Text(
            text = description.capitalize(LocalLocale.current),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            modifier = modifier,
        )
    }

    @Composable
    private fun ExpansionIcon(modifier: Modifier) = IconProjector(
        interactor = expansionIconInteractor(isExpanded)
    ).Projection(
        modifier = modifier
    )
}