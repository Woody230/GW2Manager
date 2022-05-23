package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.main.model.cache.ClearResources
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.CacheViewModel
import com.bselzer.ktx.compose.resource.images.painter
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.background.image.BackgroundImage
import com.bselzer.ktx.compose.ui.layout.checkbox.CheckboxInteractor
import com.bselzer.ktx.compose.ui.layout.column.spacedColumnProjector
import com.bselzer.ktx.compose.ui.layout.description.DescriptionInteractor
import com.bselzer.ktx.compose.ui.layout.image.ImageInteractor
import com.bselzer.ktx.compose.ui.layout.preference.PreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.checkbox.CheckboxPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.checkbox.CheckboxPreferenceProjector
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import com.bselzer.ktx.function.collection.buildArray

class CacheComposition(model: CacheViewModel) : ViewModelComposition<CacheViewModel>(model) {
    @Composable
    override fun CacheViewModel.Content() = BackgroundImage(
        modifier = Modifier.fillMaxSize(),
        painter = relativeBackgroundPainter
    ) {
        spacedColumnProjector(thickness = padding).Projection(
            modifier = Modifier.padding(paddingValues).verticalScroll(rememberScrollState()),
            content = buildArray {
                resources.forEach { resource ->
                    add { Projection(resource) }
                }
            }
        )
    }

    @Composable
    private fun CacheViewModel.interactor(resource: ClearResources) = CheckboxPreferenceInteractor(
        checkbox = CheckboxInteractor(
            checked = isSelected(resource.type),
            onCheckedChange = { checked ->
                if (checked) {
                    select(resource.type)
                } else {
                    deselect(resource.type)
                }
            }
        ),
        preference = PreferenceInteractor(
            image = ImageInteractor(
                painter = resource.image.painter(),
                contentDescription = resource.subtitle.localized()
            ),
            description = DescriptionInteractor(
                title = TextInteractor(resource.title.localized()),
                subtitle = TextInteractor(resource.subtitle.localized())
            )
        )
    )

    @Composable
    private fun CacheViewModel.Projection(cache: ClearResources) = CheckboxPreferenceProjector(
        interactor = interactor(cache),
    ).Projection()
}