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

abstract class ViewModelComposition<Model : ViewModel>(private val model: Model) {
    /**
     * Lays out the content using the [ViewModel].
     */
    @Composable
    fun Content() = model.Content()

    /**
     * Lays out the content using the [ViewModel].
     */
    @Composable
    protected abstract fun Model.Content()

    protected val padding: Dp = 25.dp
    protected val paddingValues: PaddingValues = PaddingValues(all = padding)

    /**
     * The painter for an image that will typically have text on it.
     */
    protected val relativeBackgroundPainter: Painter
        @Composable
        get() = if (LocalTheme.current == Theme.DARK) {
            Gw2Resources.images.gw2_bloodstone_night.painter()
        } else {
            Gw2Resources.images.gw2_ice.painter()
        }

    /**
     * The painter for an image that will typically NOT have text on it.
     */
    protected val absoluteBackgroundPainter: Painter
        @Composable
        get() = Gw2Resources.images.gw2_two_sylvari.painter()
}