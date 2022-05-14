package com.bselzer.gw2.manager.common.ui.layout.splash.content

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.InitializationViewModel
import com.bselzer.ktx.compose.ui.layout.description.DescriptionPresenter
import com.bselzer.ktx.compose.ui.layout.description.DescriptionProjector
import com.bselzer.ktx.compose.ui.layout.image.ImageInteractor
import com.bselzer.ktx.compose.ui.layout.image.ImageProjector
import com.bselzer.ktx.compose.ui.layout.image.backgroundImagePresenter
import com.bselzer.ktx.compose.ui.layout.text.TextPresenter

class InitializationComposition(
    private val onFinish: () -> Unit
) : ViewModelComposition<InitializationViewModel>() {
    @Composable
    override fun Content(model: InitializationViewModel) = model.run {
        Initialize(onFinish)

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            ImageProjector(
                presenter = backgroundImagePresenter(),
                interactor = ImageInteractor(
                    painter = model.absoluteBackgroundPainter,
                    contentDescription = null
                )
            ).Projection()

            Box(modifier = Modifier.fillMaxWidth()) {
                ImageProjector(
                    presenter = backgroundImagePresenter(),
                    interactor = ImageInteractor(
                        painter = model.relativeBackgroundPainter,
                        contentDescription = null
                    ),
                ).Projection()

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val description by remember { description }
                    DescriptionProjector(
                        interactor = description ?: noDescription,
                        presenter = DescriptionPresenter(title = TextPresenter(fontSize = 30.sp))
                    ).Projection(modifier = Modifier.padding(vertical = 10.dp))

                    Spacer(modifier = Modifier.height(10.dp))

                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize(0.15f)
                    )
                }
            }
        }
    }
}