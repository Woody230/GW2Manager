package com.bselzer.gw2.manager.common.ui.layout.common

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.dependency.LocalTheme
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.ui.layout.background.image.BackgroundImage
import com.bselzer.ktx.compose.ui.layout.background.image.backgroundImagePresenter
import com.bselzer.ktx.compose.ui.layout.image.ImagePresenter

/**
 * The painter for an image that will typically have text on it.
 */
private val relativeBackgroundPainter: Painter
    @Composable
    get() = if (LocalTheme.current == Theme.DARK) {
        AppResources.images.bloodstone_night_no_fire.painter()
    } else {
        AppResources.images.ice.painter()
    }

/**
 * The painter for an image that will typically NOT have text on it.
 */
private val absoluteBackgroundPainter: Painter
    @Composable
    get() = AppResources.images.two_sylvari.painter()

private val relativeBackgroundPresenter
    @Composable
    get() = backgroundImagePresenter() merge ImagePresenter(alignment = Alignment.Center)

private val absoluteBackgroundPresenter
    @Composable
    get() = backgroundImagePresenter()

/**
 * Lays out an image with [content] that typically has text.
 */
@Composable
fun RelativeBackgroundImage(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) = BackgroundImage(
    modifier = modifier,
    painter = relativeBackgroundPainter,
    presenter = relativeBackgroundPresenter,
    contentAlignment = contentAlignment,
    content = content
)

/**
 * Lays out an image with [content] that typically does NOT have text.
 */
@Composable
fun AbsoluteBackgroundImage(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) = BackgroundImage(
    modifier = modifier,
    painter = absoluteBackgroundPainter,
    presenter = absoluteBackgroundPresenter,
    contentAlignment = contentAlignment,
    content = content
)