package com.bselzer.gw2.manager.common.ui.layout.image

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import com.bselzer.gw2.manager.common.dependency.LocalDependencies
import com.bselzer.ktx.compose.image.ui.layout.async.AsyncImageInteractor
import com.bselzer.ktx.compose.image.ui.layout.async.AsyncImagePresenter
import com.bselzer.ktx.compose.image.ui.layout.async.AsyncImageProjector
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.image.ImagePresenter
import com.bselzer.ktx.compose.ui.layout.progress.indicator.ProgressIndicatorInteractor
import com.bselzer.ktx.logging.Logger
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.ImageDescUrl

data class AsyncImage(
    val enabled: Boolean = true,
    val image: ImageDesc?,
    val size: DpSize,
    val color: Color? = null,
    val description: StringDesc? = null,
    val alpha: Float = DefaultAlpha
)

enum class ProgressIndication {
    ENABLED,
    DISABLED
}

@Composable
fun AsyncImage.Content(
    modifier: Modifier = Modifier,
    progressIndication: ProgressIndication = ProgressIndication.ENABLED
) {
    val link = when (image) {
        is ImageDescUrl -> {
            image.url
        }
        else -> {
            Logger.w { "Expected image to be ImageDescUrl but found $image" }
            null
        }
    }

    if (enabled && !link.isNullOrBlank()) {
        val dependencies = LocalDependencies.current
        AsyncImageProjector(
            // TODO placeholder drawables for certain images?
            interactor = AsyncImageInteractor(
                url = link,
                getImage = { url -> dependencies.repositories.image.getImage(url) },
                contentDescription = description?.localized(),
                loadingProgress = if (progressIndication == ProgressIndication.ENABLED) ProgressIndicatorInteractor.Default else null
            ),
            presenter = AsyncImagePresenter(
                image = ImagePresenter(
                    alpha = alpha,
                    contentScale = ContentScale.Fit,

                    // Multiply the given color with the existing image (which is most likely a neutral gray).
                    colorFilter = color?.let { filterColor -> ColorFilter.lighting(filterColor, Color.Transparent) }
                )
            )
        ).Projection(
            modifier = modifier.size(size)
        )
    }
}