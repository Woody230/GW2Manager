package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.layout.common.RelativeBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.LicenseViewModel
import com.bselzer.ktx.compose.ui.layout.card.CardPresenter
import com.bselzer.ktx.library.LibraryInteractor
import com.bselzer.ktx.library.LibraryPresenter
import com.bselzer.ktx.library.LibraryProjector

class LicenseComposition(model: LicenseViewModel) : MainChildComposition<LicenseViewModel>(model) {
    @Composable
    override fun LicenseViewModel.Content(modifier: Modifier) = RelativeBackgroundImage(
        modifier = Modifier.fillMaxSize().then(modifier),
    ) {
        projector().Projection(modifier = Modifier.padding(8.dp))
    }

    @Composable
    private fun LicenseViewModel.projector() = LibraryProjector(
        interactor = interactor(),
        presenter = presenter()
    )

    @Composable
    private fun LicenseViewModel.interactor() = LibraryInteractor(
        libraries = libraries
    )

    @Composable
    private fun presenter() = LibraryPresenter(
        container = CardPresenter(
            // Use the relative background instead.
            backgroundColor = Color.Transparent,

            // Disable the shadow.
            elevation = 0.dp
        )
    )
}