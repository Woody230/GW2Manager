package com.bselzer.gw2.manager.common.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
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

    val relativeBackgroundPainter: Painter
        @Composable
        get() = if (LocalTheme.current == Theme.DARK) {
            Gw2Resources.images.gw2_bloodstone_night.painter()
        } else {
            Gw2Resources.images.gw2_ice.painter()
        }

    val absoluteBackgroundPainter: Painter
        @Composable
        get() = Gw2Resources.images.gw2_two_sylvari.painter()
}