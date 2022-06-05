package com.bselzer.gw2.manager.common.ui.base

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.dependency.LocalTheme
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.ui.layout.background.image.backgroundImagePresenter
import com.bselzer.ktx.compose.ui.layout.image.ImagePresenter

abstract class ViewModelComposition<Model : ViewModel>(protected val model: Model) {
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
     * For dark theme, use the top of the image which has a more uniform dark color compared to the center which has a fire.
     */
    private val relativeBackgroundAlignment
        @Composable
        get() = if (LocalTheme.current == Theme.DARK) {
            Alignment.TopCenter
        } else {
            Alignment.Center
        }

    /**
     * The painter for an image that will typically have text on it.
     */
    protected val relativeBackgroundPainter: Painter
        @Composable
        get() = if (LocalTheme.current == Theme.DARK) {
            AppResources.images.gw2_bloodstone_night.painter()
        } else {
            AppResources.images.gw2_ice.painter()
        }

    /**
     * The painter for an image that will typically NOT have text on it.
     */
    protected val absoluteBackgroundPainter: Painter
        @Composable
        get() = AppResources.images.gw2_two_sylvari.painter()

    protected val relativeBackgroundPresenter
        @Composable
        get() = backgroundImagePresenter() merge ImagePresenter(alignment = relativeBackgroundAlignment)

    protected val absoluteBackgroundPresenter
        @Composable
        get() = backgroundImagePresenter()
}