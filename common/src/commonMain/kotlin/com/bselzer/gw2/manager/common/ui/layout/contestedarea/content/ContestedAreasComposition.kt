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
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.model.ContestedObjective
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.model.ContestedObjectives
import com.bselzer.gw2.manager.common.ui.layout.contestedarea.viewmodel.ContestedAreasViewModel
import com.bselzer.gw2.manager.common.ui.layout.image.AsyncImage
import com.bselzer.gw2.manager.common.ui.layout.image.Content
import com.bselzer.ktx.compose.resource.strings.localized

interface ContestedAreasComposition<Model : ContestedAreasViewModel> {
    @Composable
    fun Model.ContestedAreasContent(
        modifier: Modifier = Modifier
    ) = Column(modifier) {
        contestedObjectives.forEach { contestedObjectives ->
            contestedObjectives.Content()
        }
    }

    @Composable
    private fun ContestedObjectives.Content() = Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        objectives.forEach { objective ->
            objective.Content()
        }

        Text(text = ppt.localized(), color = color, fontSize = 32.sp)
    }

    @Composable
    private fun ContestedObjective.Content() = Column(
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