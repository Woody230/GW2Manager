package com.bselzer.gw2.manager.common.ui.layout.main.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.main.model.cache.CacheClear
import com.bselzer.gw2.manager.common.ui.layout.main.viewmodel.CacheViewModel
import com.bselzer.ktx.compose.resource.images.painter
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
import dev.icerock.moko.resources.compose.localized

class CacheComposition : ViewModelComposition<CacheViewModel>() {
    @Composable
    override fun Content(model: CacheViewModel) = model.run {
        BackgroundImage(
            modifier = Modifier.fillMaxSize(),
            painter = relativeBackgroundPainter
        ) {
            // TODO app bar perform cache clear and show snackbar
            // TODO select all
            spacedColumnProjector(thickness = 25.dp).Projection(
                modifier = Modifier.padding(25.dp),
                content = buildArray {
                    listOf(continent, guild, image, wvw).forEach { cache ->
                        add { Projection(cache) }
                    }
                }
            )
        }
    }

    @Composable
    private fun CacheViewModel.interactor(cache: CacheClear) = CheckboxPreferenceInteractor(
        checkbox = CheckboxInteractor(
            checked = selected.contains(cache),
            onCheckedChange = { checked ->
                if (checked) {
                    select(cache)
                } else {
                    deselect(cache)
                }
            }
        ),
        preference = PreferenceInteractor(
            image = ImageInteractor(
                painter = cache.image.painter(),
                contentDescription = cache.subtitle.localized()
            ),
            description = DescriptionInteractor(
                title = TextInteractor(cache.title.localized()),
                subtitle = TextInteractor(cache.subtitle.localized())
            )
        )
    )

    @Composable
    private fun CacheViewModel.Projection(cache: CacheClear) = CheckboxPreferenceProjector(
        interactor = interactor(cache),
    ).Projection()
}