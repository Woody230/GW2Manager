package com.bselzer.gw2.manager.common.ui.layout.custom.objective.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.bselzer.gw2.manager.common.ui.base.ModelComposition
import com.bselzer.gw2.manager.common.ui.layout.custom.objective.model.CoreMatchData
import com.bselzer.ktx.resource.strings.localized

class CoreMatchComposition(
    model: CoreMatchData
) : ModelComposition<CoreMatchData>(model) {
    @Composable
    override fun CoreMatchData.Content(modifier: Modifier) = Column(
        modifier = Modifier.fillMaxWidth().then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Name()
        Map()
        Owner()
        Flipped()
    }

    // TODO images alongside the text?
    @Composable
    private fun CoreMatchData.Name() = Text(text = name.localized(), textAlign = TextAlign.Center)

    @Composable
    private fun CoreMatchData.Map() = map?.let { map ->
        Text(text = map.name.localized(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = map.color)
    }

    @Composable
    private fun CoreMatchData.Owner() = owner?.let { owner ->
        Text(text = owner.name.localized(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = owner.color)
    }

    @Composable
    private fun CoreMatchData.Flipped() = flipped?.let { flipped ->
        Text(text = flipped.localized(), textAlign = TextAlign.Center)
    }
}