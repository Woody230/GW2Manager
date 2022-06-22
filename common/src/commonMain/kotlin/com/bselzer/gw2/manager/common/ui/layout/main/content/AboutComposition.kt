package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.layout.common.RelativeBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.AboutViewModel
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.column.spacedColumnProjector
import com.bselzer.ktx.compose.ui.layout.description.DescriptionInteractor
import com.bselzer.ktx.compose.ui.layout.description.DescriptionProjector
import com.bselzer.ktx.function.collection.buildArray

class AboutComposition(model: AboutViewModel) : MainChildComposition<AboutViewModel>(model) {
    @Composable
    override fun AboutViewModel.Content() = RelativeBackgroundImage(
        modifier = Modifier.fillMaxSize(),
    ) {
        spacedColumnProjector(thickness = padding).Projection(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            content = buildArray {
                add { VersionName() }
                add { VersionCode() }
                add { LegalNotice() }
            }
        )
    }

    @Composable
    private fun AboutViewModel.VersionCode() = DescriptionProjector(
        interactor = DescriptionInteractor(
            title = versionResources.code.title.localized(),
            subtitle = versionResources.code.subtitle.localized()
        )
    ).Projection()

    @Composable
    private fun AboutViewModel.VersionName() = DescriptionProjector(
        interactor = DescriptionInteractor(
            title = versionResources.name.title.localized(),
            subtitle = versionResources.name.subtitle.localized()
        )
    ).Projection()

    @Composable
    private fun AboutViewModel.LegalNotice() = DescriptionProjector(
        interactor = DescriptionInteractor(
            title = noticeResources.title.localized(),
            subtitle = noticeResources.subtitle.localized()
        )
    ).Projection()
}