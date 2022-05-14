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
import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.dependency.LocalTheme
import com.bselzer.gw2.manager.common.ui.base.Composition
import com.bselzer.gw2.manager.common.ui.layout.splash.viewmodel.InitializationViewModel
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.ui.layout.description.DescriptionPresenter
import com.bselzer.ktx.compose.ui.layout.description.DescriptionProjector
import com.bselzer.ktx.compose.ui.layout.image.ImageInteractor
import com.bselzer.ktx.compose.ui.layout.image.ImageProjector
import com.bselzer.ktx.compose.ui.layout.image.backgroundImagePresenter
import com.bselzer.ktx.compose.ui.layout.text.TextPresenter

class InitializationComposition(
    private val onFinish: () -> Unit
) : Composition<InitializationViewModel> {
    @Composable
    override fun Content(model: InitializationViewModel) = model.run {
        Initialize(onFinish)

        Box(modifier = Modifier.fillMaxWidth()) {
            ImageProjector(
                presenter = backgroundImagePresenter(),
                interactor = ImageInteractor(
                    painter = if (LocalTheme.current == Theme.DARK) Gw2Resources.images.gw2_bloodstone_night.painter() else Gw2Resources.images.gw2_ice.painter(),
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