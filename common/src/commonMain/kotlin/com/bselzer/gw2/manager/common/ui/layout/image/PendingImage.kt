package com.bselzer.gw2.manager.common.ui.layout.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import com.bselzer.gw2.manager.common.dependency.KodeinTransaction
import com.bselzer.ktx.compose.image.cache.instance.ImageCache
import com.bselzer.ktx.compose.image.ui.rememberImagePainter
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.unit.toDp
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.ImageDescUrl

// TODO use image projection
data class PendingImage(
    val enabled: Boolean = true,
    val image: ImageDesc,
    val width: Int,
    val height: Int,
    val color: Color? = null,
    val description: StringDesc? = null,
    val alpha: Float = DefaultAlpha
)

@Composable
fun PendingImage.Content(
    modifier: Modifier = Modifier,
    transaction: KodeinTransaction,
    cache: ImageCache
) {
    // TODO check if image desc resource
    val link = if (image is ImageDescUrl) image.url else null
    if (enabled && !link.isNullOrBlank()) {
        val painter = rememberImagePainter(
            url = link,
            getImage = { url ->
                transaction.transaction {
                    with(cache) { getImage(url) }
                }
            }
        )

        if (painter == null) {
            // TODO placeholder drawables for certain images?
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(0.15f)
            )
        } else {
            Image(
                contentDescription = description?.localized(),
                painter = painter,
                contentScale = ContentScale.Fit,
                alpha = alpha,
                modifier = modifier.size(width = width.toDp(), height = height.toDp()),

                // Multiply the given color with the existing image (which is most likely a neutral gray).
                colorFilter = (color ?: this.color)?.let { filterColor -> ColorFilter.lighting(filterColor, Color.Transparent) }
            )
        }
    }
}