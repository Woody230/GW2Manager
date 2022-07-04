package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.Content
import com.bselzer.gw2.manager.common.ui.layout.common.ImageImpl
import com.bselzer.gw2.manager.common.ui.layout.common.ProgressIndication
import com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model.UpgradeTierIcon

class UpgradeTierIconComposition(
    model: UpgradeTierIcon
) : ModelComposition<UpgradeTierIcon>(model) {
    @Composable
    override fun UpgradeTierIcon.Content(modifier: Modifier) = ImageImpl(
        image = link,
        color = color,
        alpha = alpha.collectAsState(DefaultAlpha).value
    ).Content(
        modifier = modifier,
        progressIndication = ProgressIndication.ENABLED,
        size = DpSize(75.dp, 75.dp),
    )
}