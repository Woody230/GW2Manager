package com.bselzer.library.kotlin.extension.compose.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

/**
 * Displays a cropped background image.
 *
 * @param painter the painter
 * @param modifier the modifier for handling size
 * @param alignment where to focus the cropping
 */
@Composable
internal fun ShowBackground(painter: Painter, modifier: Modifier, alignment: Alignment = Alignment.Center) = Image(
    painter = painter,
    contentDescription = null,
    modifier = modifier,
    contentScale = ContentScale.Crop,
    alignment = alignment
)

/**
 * Displays a cropped background image.
 *
 * @param painter the painter
 * @param alignment where to focus the cropping
 */
@Composable
fun ShowBackground(painter: Painter, alignment: Alignment = Alignment.Center) =
    ShowBackground(painter = painter, modifier = Modifier.fillMaxSize(), alignment = alignment)

/**
 * Displays a cropped background image.
 *
 * @param drawableId the id of the drawable to paint
 * @param alignment where to focus the cropping
 */
@Composable
fun ShowBackground(@DrawableRes drawableId: Int, alignment: Alignment = Alignment.Center) =
    ShowBackground(painter = painterResource(id = drawableId), alignment = alignment)