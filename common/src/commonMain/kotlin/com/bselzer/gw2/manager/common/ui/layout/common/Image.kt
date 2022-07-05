package com.bselzer.gw2.manager.common.ui.layout.common

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
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.image.ImageInteractor
import com.bselzer.ktx.compose.ui.layout.image.ImagePresenter
import com.bselzer.ktx.compose.ui.layout.image.ImageProjector
import com.bselzer.ktx.compose.ui.layout.progress.indicator.ProgressIndicatorInteractor
import com.bselzer.ktx.logging.Logger
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.ImageDescResource
import dev.icerock.moko.resources.desc.image.ImageDescUrl
import dev.icerock.moko.resources.desc.image.asImageUrl

data class ImageImpl(
    override val enabled: Boolean = true,
    override val image: ImageDesc?,
    override val color: Color? = null,
    override val description: StringDesc? = null,
    override val alpha: Float = DefaultAlpha
) : Image

interface ImageAdapter : Image {
    override val enabled: Boolean
        get() = true

    override val color: Color?
        get() = null

    override val description: StringDesc?
        get() = null

    override val alpha: Float
        get() = DefaultAlpha
}

interface Image {
    val enabled: Boolean
    val image: ImageDesc?
    val color: Color?
    val description: StringDesc?
    val alpha: Float
}

enum class ProgressIndication {
    ENABLED,
    DISABLED
}

fun String.image(): Image = ImageImpl(image = asImageUrl())

@Composable
fun Image.Content(
    modifier: Modifier = Modifier,
    size: DpSize,
    progressIndication: ProgressIndication = ProgressIndication.ENABLED
) {
    if (!enabled) {
        return
    }

    val presenter = ImagePresenter(
        alpha = alpha,
        contentScale = ContentScale.Fit,

        // Multiply the given color with the existing image (which is most likely a neutral gray).
        colorFilter = color?.let { filterColor -> ColorFilter.lighting(filterColor, Color.Transparent) }
    )

    val combinedModifier = modifier.size(size)
    when (val image = image) {
        is ImageDescUrl -> {
            if (image.url.isNotBlank()) {
                Link(combinedModifier, image.url, progressIndication, presenter)
            }
        }
        is ImageDescResource -> {
            Resource(combinedModifier, image.resource, presenter)
        }
        else -> {
            Logger.w { "Expected image to be for an ImageDescUrl or ImageDescResource but found $image." }
        }
    }
}

@Composable
private fun Image.Resource(
    modifier: Modifier,
    resource: ImageResource,
    presenter: ImagePresenter,
) = ImageProjector(
    presenter = presenter,
    interactor = ImageInteractor(
        painter = resource.painter(),
        contentDescription = description?.localized()
    )
).Projection(modifier = modifier)

@Composable
private fun Image.Link(
    modifier: Modifier,
    link: String,
    progressIndication: ProgressIndication,
    presenter: ImagePresenter
) {
    val dependencies = LocalDependencies.current
    AsyncImageProjector(
        presenter = AsyncImagePresenter(
            image = presenter
        ),

        // TODO placeholder drawables for certain images?
        interactor = AsyncImageInteractor(
            url = link,
            getImage = { url -> dependencies.repositories.image.getImage(url) },
            contentDescription = description?.localized(),
            loadingProgress = if (progressIndication == ProgressIndication.ENABLED) ProgressIndicatorInteractor.Default else null
        ),
    ).Projection(
        modifier = modifier
    )
}