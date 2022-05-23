package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.LicenseViewModel
import com.bselzer.ktx.compose.ui.layout.background.image.BackgroundImage
import com.bselzer.ktx.compose.ui.layout.card.CardPresenter
import com.bselzer.ktx.library.LibraryInteractor
import com.bselzer.ktx.library.LibraryPresenter
import com.bselzer.ktx.library.LibraryProjector

class LicenseComposition(model: LicenseViewModel) : MainChildComposition<LicenseViewModel>(model) {
    @Composable
    override fun LicenseViewModel.Content() = BackgroundImage(
        modifier = Modifier.fillMaxSize(),
        painter = relativeBackgroundPainter,
    ) {
        Libraries()
    }

    @Composable
    private fun LicenseViewModel.Libraries() = LibraryProjector(
        interactor = LibraryInteractor(
            libraries = libraries
        ),
        presenter = LibraryPresenter(
            container = CardPresenter(
                // Use the relative background instead.
                backgroundColor = Color.Transparent,

                // Disable the shadow.
                elevation = 0.dp
            )
        )
    ).Projection(modifier = Modifier.padding(8.dp))
}