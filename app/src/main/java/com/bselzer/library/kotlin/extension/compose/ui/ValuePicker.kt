package com.bselzer.library.kotlin.extension.compose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

/**
 * @see <a href="https://gist.github.com/vganin/a9a84653a9f48a2d669910fbd48e32d5">gist by vganin</a>
 */

/**
 * Lays out a value picker.
 *
 * @param state the state of the value
 * @param values the possible state values
 * @param labels the displayable values for each of the [values]
 * @param modifier the modifier applying to the value picker
 * @param textStyle the style of the text for displaying the value
 */
@Composable
fun <T> ValuePicker(
    state: MutableState<T>,
    values: List<T>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
) {
    val currentValue = state.value
    val index = values.indexOfFirst { value -> value == currentValue }
    Column(
        modifier = modifier
            .wrapContentSize()
    ) {
        IconButton(onClick = { values.getOrNull(index + 1)?.let { value -> state.value = value } }) {
            Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Up")
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            val baseLabelModifier = Modifier.align(Alignment.Center)
            ProvideTextStyle(textStyle) {
                Text(
                    text = labels.getOrNull(index) ?: throw IllegalStateException("Missing a label at index $index for the value ${currentValue}."),
                    modifier = baseLabelModifier
                )
            }
        }

        IconButton(onClick = { values.getOrNull(index - 1)?.let { value -> state.value = value } }) {
            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Down")
        }
    }
}