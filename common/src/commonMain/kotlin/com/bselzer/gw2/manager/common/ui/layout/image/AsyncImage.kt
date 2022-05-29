package com.bselzer.gw2.manager.common.ui.layout.image

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import com.bselzer.gw2.manager.common.dependency.LocalDependencies
import com.bselzer.ktx.compose.image.ui.layout.async.AsyncImageInteractor
import com.bselzer.ktx.compose.image.ui.layout.async.AsyncImagePresenter
import com.bselzer.ktx.compose.image.ui.layout.async.AsyncImageProjector
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.image.ImagePresenter
import com.bselzer.ktx.compose.ui.unit.toDp
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.ImageDescUrl

// TODO use image projection
data class AsyncImage(
    val enabled: Boolean = true,
    val image: ImageDesc?,
    val width: Int,
    val height: Int,
    val color: Color? = null,
    val description: StringDesc? = null,
    val alpha: Float = DefaultAlpha
)

@Composable
fun AsyncImage.Content(
    modifier: Modifier = Modifier
) {
    val dependencies = LocalDependencies.current

    // TODO check if image desc resource
    val link = if (image is ImageDescUrl) image.url else null
    if (enabled && !link.isNullOrBlank()) {
        AsyncImageProjector(
            // TODO placeholder drawables for certain images?
            interactor = AsyncImageInteractor(
                url = link,
                getImage = { url ->
                    dependencies.repositories.image.getImage(url)
                },
                contentDescription = description?.localized()
            ),
            presenter = AsyncImagePresenter(
                image = ImagePresenter(
                    alpha = alpha,
                    contentScale = ContentScale.Fit,

                    // Multiply the given color with the existing image (which is most likely a neutral gray).
                    colorFilter = (color ?: this.color)?.let { filterColor -> ColorFilter.lighting(filterColor, Color.Transparent) }
                )
            )
        ).Projection(
            modifier = modifier.size(
                width = width.toDp(),
                height = height.toDp()
            )
        )
    }
}