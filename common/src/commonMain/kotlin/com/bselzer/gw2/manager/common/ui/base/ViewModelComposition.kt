package com.bselzer.gw2.manager.common.ui.base

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.dependency.LocalTheme
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.compose.resource.images.painter

abstract class ViewModelComposition<Model : ViewModel> {
    /**
     * Lays out the content using the [ViewModel].
     */
    @Composable
    abstract fun Content(model: Model)

    protected val padding: Dp = 25.dp
    protected val paddingValues: PaddingValues = PaddingValues(all = padding)

    protected val relativeBackgroundPainter: Painter
        @Composable
        get() = if (LocalTheme.current == Theme.DARK) {
            Gw2Resources.images.gw2_bloodstone_night.painter()
        } else {
            Gw2Resources.images.gw2_ice.painter()
        }

    protected val absoluteBackgroundPainter: Painter
        @Composable
        get() = Gw2Resources.images.gw2_two_sylvari.painter()
}