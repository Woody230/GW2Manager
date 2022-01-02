package com.bselzer.gw2.manager.common.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bselzer.ktx.compose.image.ui.rememberImagePainter
import com.bselzer.ktx.compose.ui.unit.toDp

interface ImageState {
    val enabled: Boolean
    val link: String?
    val width: Int
    val height: Int
    val color: Color?
    val description: String?
    val alpha: Float
}

abstract class ImageStateAdapter : ImageState {
    override val enabled: Boolean = true
    override val color: Color? = null
    override val description: String? = null
    override val alpha: Float = DefaultAlpha
}

@Composable
fun ImageState.ImageContent(modifier: Modifier = Modifier, isPixel: Boolean = true) {
    if (enabled && !link.isNullOrBlank()) {
        val width = if (isPixel) width.toDp() else width.dp
        val height = if (isPixel) height.toDp() else height.dp

        val painter = rememberImagePainter(url = link)
        if (painter == null) {
            // TODO placeholder drawables for certain images?
            CircularProgressIndicator(
                modifier = Modifier.size(width = width, height = height)
            )
        } else {
            Image(
                contentDescription = description,
                painter = painter,
                contentScale = ContentScale.Fit,
                alpha = alpha,
                modifier = modifier.size(width = width, height = height),

                // Multiply the given color with the existing image (which is most likely a neutral gray).
                colorFilter = color?.let { color -> ColorFilter.lighting(color, Color.Transparent) }
            )
        }
    }
}