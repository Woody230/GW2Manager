package com.bselzer.gw2.manager.common.ui.layout.common

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bselzer.ktx.compose.ui.layout.column.ColumnPresenter
import com.bselzer.ktx.compose.ui.layout.column.ColumnProjector
import com.bselzer.ktx.compose.ui.layout.column.spacedColumnProjector
import com.bselzer.ktx.compose.ui.layout.merge.TriState

val InfoSpacedColumn: ColumnProjector
    @Composable
    get() = spacedColumnProjector(
        thickness = 10.dp,
        presenter = ColumnPresenter(
            prepend = TriState.TRUE,
            append = TriState.TRUE,
            horizontalAlignment = Alignment.CenterHorizontally
        )
    )

/**
 * Lays out a card wrapping the underlying [content].
 */
@Composable
fun InfoCard(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) = BorderedCard(
    content = content,
    modifier = Modifier
        .fillMaxWidth(.90f)
        .wrapContentHeight()
        .then(modifier)
)