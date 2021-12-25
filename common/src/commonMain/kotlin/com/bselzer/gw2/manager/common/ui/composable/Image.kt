package com.bselzer.gw2.manager.common.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
}

@Composable
fun ImageState.ImageContent(modifier: Modifier = Modifier, isPixel: Boolean = true) {
    if (enabled && !link.isNullOrBlank()) {
        // TODO placeholder image or progress bar as appropriate for missing images
        rememberImagePainter(url = link)?.let { painter ->
            Image(
                contentDescription = description,
                painter = painter,
                contentScale = ContentScale.Fit,
                modifier = modifier.size(width = if (isPixel) width.toDp() else width.dp, height = if (isPixel) height.toDp() else height.dp),
                colorFilter = color?.let { color -> ColorFilter.lighting(color, Color.Transparent) }
            )
        }
    }
}