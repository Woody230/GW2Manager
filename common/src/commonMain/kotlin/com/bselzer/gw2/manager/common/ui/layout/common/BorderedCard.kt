package com.bselzer.gw2.manager.common.ui.layout.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun BorderedCard(
    modifier: Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val border = 3.dp
    Card(
        elevation = 10.dp,
        shape = RectangleShape,
        modifier = Modifier
            .border(width = border, color = Color.Black)
            .padding(all = border)
            .then(modifier)
    ) {
        RelativeBackgroundImage(
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}