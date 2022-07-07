package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bselzer.gw2.manager.common.ui.layout.common.RelativeBackgroundImage
import com.bselzer.gw2.manager.common.ui.layout.main.model.cache.ClearResources
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.CacheViewModel
import com.bselzer.ktx.compose.ui.layout.checkbox.CheckboxInteractor
import com.bselzer.ktx.compose.ui.layout.column.spacedColumnProjector
import com.bselzer.ktx.compose.ui.layout.preference.PreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.checkbox.CheckboxPreferenceInteractor
import com.bselzer.ktx.compose.ui.layout.preference.checkbox.CheckboxPreferenceProjector
import com.bselzer.ktx.function.collection.buildArray
import com.bselzer.ktx.resource.images.painter
import com.bselzer.ktx.resource.strings.localized

class CacheComposition(model: CacheViewModel) : MainChildComposition<CacheViewModel>(model) {

    @Composable
    override fun CacheViewModel.Content(modifier: Modifier) = RelativeBackgroundImage(
        modifier = Modifier.fillMaxSize().then(modifier),
    ) {
        Caches()
    }

    @Composable
    private fun CacheViewModel.Caches() = spacedColumnProjector(
        thickness = padding
    ).Projection(
        modifier = Modifier.padding(paddingValues).verticalScroll(rememberScrollState()),
        content = buildArray {
            resources.forEach { resource ->
                add { projector(resource).Projection() }
            }
        }
    )


    @Composable
    private fun CacheViewModel.projector(resource: ClearResources) = CheckboxPreferenceProjector(
        interactor = CheckboxPreferenceInteractor(
            checkbox = checkboxInteractor(resource),
            preference = preferenceInteractor(resource)
        )
    )

    @Composable
    private fun CacheViewModel.checkboxInteractor(resource: ClearResources) = CheckboxInteractor(
        checked = isSelected(resource.type),
        onCheckedChange = { checked ->
            if (checked) {
                select(resource.type)
            } else {
                deselect(resource.type)
            }
        }
    )

    @Composable
    private fun preferenceInteractor(resource: ClearResources) = PreferenceInteractor(
        painter = resource.image.painter(),
        title = resource.title.localized(),
        subtitle = resource.subtitle.localized()
    )
}