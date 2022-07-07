package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.Content
import com.bselzer.gw2.manager.common.ui.layout.common.ImageImpl
import com.bselzer.gw2.manager.common.ui.layout.common.ProgressIndication
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.Upgrade
import com.bselzer.ktx.resource.strings.localized

class UpgradeComposition(
    model: Upgrade
) : ModelComposition<Upgrade>(model) {
    @Composable
    override fun Upgrade.Content(modifier: Modifier) = Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon()
        Spacer(modifier = Modifier.width(25.dp))
        Description()
    }

    @Composable
    private fun Upgrade.Icon() = ImageImpl(
        image = link,
        alpha = alpha.collectAsState(DefaultAlpha).value
    ).Content(
        progressIndication = ProgressIndication.ENABLED,
        size = DpSize(50.dp, 50.dp),
    )

    @Composable
    private fun Upgrade.Description() = Column {
        Text(text = name.localized(), style = MaterialTheme.typography.subtitle1)
        Text(text = description.localized(), style = MaterialTheme.typography.body2)
    }
}