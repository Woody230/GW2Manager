package com.bselzer.gw2.manager.common.ui.layout.custom.objective.content

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.objective.model.CoreData
import com.bselzer.ktx.compose.ui.intl.LocalLocale
import com.bselzer.ktx.compose.ui.layout.centeredtext.CenteredTextInteractor
import com.bselzer.ktx.compose.ui.layout.centeredtext.CenteredTextPresenter
import com.bselzer.ktx.compose.ui.layout.centeredtext.CenteredTextProjector
import com.bselzer.ktx.compose.ui.layout.text.TextInteractor
import com.bselzer.ktx.compose.ui.layout.text.TextPresenter
import com.bselzer.ktx.compose.ui.layout.text.textInteractor
import com.bselzer.ktx.resource.strings.localized
import dev.icerock.moko.resources.desc.StringDesc

class CoreComposition(
    model: CoreData
) : ModelComposition<CoreData>(model) {
    private val dataPoints = with(model) {
        listOf(pointsPerTick, pointsPerCapture, yaks, progression)
    }

    @Composable
    override fun CoreData.Content(modifier: Modifier) = Column(
        modifier = modifier
    ) {
        dataPoints.forEach { dataPoint -> dataPoint.Row() }
    }

    @Composable
    private fun Pair<StringDesc, StringDesc>?.Row() = this?.let {
        projector().Projection()
    }

    @Composable
    private fun Pair<StringDesc, StringDesc>.interactor() = CenteredTextInteractor(
        // TODO divider instead of colon? separate rows for first/secondary text?
        start = TextInteractor(first.localized() + ":"),
        end = second.localized().capitalize(LocalLocale.current).textInteractor()
    )

    @Composable
    private fun presenter() = CenteredTextPresenter(
        start = TextPresenter(fontWeight = FontWeight.Bold)
    )

    @Composable
    private fun Pair<StringDesc, StringDesc>.projector() = CenteredTextProjector(
        interactor = interactor(),
        presenter = presenter()
    )
}