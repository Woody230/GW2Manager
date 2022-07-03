package com.bselzer.gw2.manager.common.ui.layout.contestedarea.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bselzer.gw2.manager.common.ui.base.ShouldLayoutHorizontally
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.model.ContestedObjective
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.model.ContestedPointsPerTick
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.viewmodel.ContestedAreasViewModel
import com.bselzer.gw2.manager.common.ui.layout.image.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.image.Content
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.compose.ui.layout.spacer.Spacer

interface ContestedAreasComposition<Model : ContestedAreasViewModel> {
    @Composable
    fun Model.ContestedAreasContent(
        modifier: Modifier = Modifier
    ) = Row(modifier) {
        contestedObjectives.forEach { objectives -> objectives.Content() }

        if (ShouldLayoutHorizontally) {
            Spacer(width = 20.dp)
        }

        pointsPerTick.Content()
    }

    @Composable
    @JvmName("pptContent")
    private fun List<ContestedPointsPerTick>.Content() = Column {
        forEach { ppt -> ppt.Content() }
    }

    @Composable
    private fun ContestedPointsPerTick.Content() = Text(text = ppt.localized(), color = color, fontSize = 32.sp)

    @Composable
    @JvmName("objectiveContent")
    private fun List<ContestedObjective>.Content() = Column {
        forEach { objective -> objective.Content() }
    }

    @Composable
    private fun ContestedObjective.Content() = when (ShouldLayoutHorizontally) {
        true -> HorizontalContent()
        false -> VerticalContent()
    }

    @Composable
    private fun ContestedObjective.HorizontalContent() = Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon()
        Count()
    }

    @Composable
    private fun ContestedObjective.VerticalContent() = Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon()
        Count()
    }

    @Composable
    private fun ContestedObjective.Icon() = AsyncImage(
        image = link,
        color = color,
        description = description,
        size = DpSize(50.dp, 50.dp),
    ).Content()

    @Composable
    private fun ContestedObjective.Count() = Text(text = count.localized())
}