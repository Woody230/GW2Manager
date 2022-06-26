package com.bselzer.gw2.manager.common.ui.layout.contestedarea.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.bselzer.gw2.manager.common.ui.base.ViewModelComposition
import com.bselzer.gw2.manager.common.ui.layout.common.BorderedCard
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.model.ContestedObjective
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.model.ContestedObjectives
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.viewmodel.ContestedAreasViewModel
import com.bselzer.gw2.manager.common.ui.layout.image.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.image.Content
import com.bselzer.ktx.compose.resource.strings.localized

class ContestedAreasComposition(
    model: ContestedAreasViewModel
) : ViewModelComposition<ContestedAreasViewModel>(model) {
    @Composable
    override fun ContestedAreasViewModel.Content() = BorderedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // TODO constrain
            contestedObjectives.forEach { contestedObjectives ->
                contestedObjectives.Content(Modifier)
            }
        }
    }

    @Composable
    private fun ContestedObjectives.Content(modifier: Modifier) = Row(modifier) {
        objectives.forEach { objective ->
            objective.Content(Modifier)
        }

        // TODO ppt
    }

    @Composable
    private fun ContestedObjective.Content(modifier: Modifier) = Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            image = link,
            color = color,
            description = description,
            size = DpSize(50.dp, 50.dp),
        ).Content()

        Text(text = count.localized())
    }
}