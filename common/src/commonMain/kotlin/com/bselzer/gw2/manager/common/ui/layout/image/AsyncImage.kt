package com.bselzer.gw2.manager.common.ui.layout.image

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
import com.bselzer.ktx.compose.ui.layout.modifier.presentable.ModularSize
import com.bselzer.ktx.compose.ui.layout.modifier.presentable.PreferredHeight
import com.bselzer.ktx.compose.ui.layout.modifier.presentable.PreferredWidth
import com.bselzer.ktx.compose.ui.layout.progress.indicator.ProgressIndicatorInteractor
import com.bselzer.ktx.logging.Logger
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc
import dev.icerock.moko.resources.desc.image.ImageDescResource
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
    if (!enabled) {
        return
    }

    val presenter = ImagePresenter(
        modifier = ModularSize(
            width = PreferredWidth(size.width),
            height = PreferredHeight(size.height),
        ),
        alpha = alpha,
        contentScale = ContentScale.Fit,

        // Multiply the given color with the existing image (which is most likely a neutral gray).
        colorFilter = color?.let { filterColor -> ColorFilter.lighting(filterColor, Color.Transparent) }
    )

    when (image) {
        is ImageDescUrl -> {
            if (image.url.isNotBlank()) {
                Link(modifier, image.url, progressIndication, presenter)
            }
        }
        is ImageDescResource -> {
            Resource(modifier, image.resource, presenter)
        }
        else -> {
            Logger.w { "Expected image to be for an ImageDescUrl or ImageDescResource but found $image." }
        }
    }
}

@Composable
private fun AsyncImage.Resource(
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
private fun AsyncImage.Link(
    modifier: Modifier,
    link: String,
    progressIndication: ProgressIndication,
    presenter: ImagePresenter
) {
    val dependencies = LocalDependencies.current
    AsyncImageProjector(
        presenter = AsyncImagePresenter(image = presenter),

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