package com.bselzer.library.kotlin.extension.compose.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs

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

    val coroutineScope = rememberCoroutineScope()
    val numbersColumnHeight = 36.dp
    val halvedNumbersColumnHeight = numbersColumnHeight / 2
    val halvedNumbersColumnHeightPx = LocalDensity.current.run { halvedNumbersColumnHeight.toPx() }

    fun animatedStateValue(offset: Float): Int = index - (offset / halvedNumbersColumnHeightPx).toInt()

    val animatedOffset = remember { Animatable(0f) }.apply {
        val range = values.indices
        val offsetRange = remember(state.value, range) {
            val first = -(range.last - index) * halvedNumbersColumnHeightPx
            val last = -(range.first - index) * halvedNumbersColumnHeightPx
            first..last
        }
        updateBounds(offsetRange.start, offsetRange.endInclusive)
    }
    val coercedAnimatedOffset = animatedOffset.value % halvedNumbersColumnHeightPx
    val animatedStateValue = animatedStateValue(animatedOffset.value)

    Column(
        modifier = modifier
            .wrapContentSize()
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { deltaY ->
                    coroutineScope.launch {
                        animatedOffset.snapTo(animatedOffset.value + deltaY)
                    }
                },
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        val endValue = animatedOffset.fling(
                            initialVelocity = velocity,
                            animationSpec = exponentialDecay(frictionMultiplier = 20f),
                            adjustTarget = { target ->
                                val coercedTarget = target % halvedNumbersColumnHeightPx
                                val coercedAnchors = listOf(-halvedNumbersColumnHeightPx, 0f, halvedNumbersColumnHeightPx)
                                val coercedPoint = coercedAnchors.minByOrNull { abs(it - coercedTarget) }!!
                                val base = halvedNumbersColumnHeightPx * (target / halvedNumbersColumnHeightPx).toInt()
                                coercedPoint + base
                            }
                        ).endState.value

                        val newIndex = animatedStateValue(endValue)
                        values
                            .getOrNull(newIndex)
                            ?.let { value -> state.value = value }
                        animatedOffset.snapTo(0f)
                    }
                }
            )
    ) {
        IconButton(onClick = { values.getOrNull(index + 1)?.let { value -> state.value = value } }) {
            Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Up")
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            val textModifier = Modifier.align(Alignment.Center)
            ProvideTextStyle(textStyle) {
                Text(
                    text = labels.getOrNull(animatedStateValue - 1) ?: "",
                    modifier = textModifier
                        .offset(y = -halvedNumbersColumnHeight)
                        .alpha(coercedAnimatedOffset / halvedNumbersColumnHeightPx)
                )
                Text(
                    text = labels.getOrNull(animatedStateValue) ?: throw IllegalStateException("Missing a label at index $index for the value ${currentValue}."),
                    modifier = textModifier.alpha(1 - abs(coercedAnimatedOffset) / halvedNumbersColumnHeightPx)
                )
                Text(
                    text = labels.getOrNull(animatedStateValue + 1) ?: "",
                    modifier = textModifier
                        .offset(y = halvedNumbersColumnHeight)
                        .alpha(-coercedAnimatedOffset / halvedNumbersColumnHeightPx)
                )
            }
        }

        IconButton(onClick = { values.getOrNull(index - 1)?.let { value -> state.value = value } }) {
            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Down")
        }
    }
}

private suspend fun Animatable<Float, AnimationVector1D>.fling(
    initialVelocity: Float,
    animationSpec: DecayAnimationSpec<Float>,
    adjustTarget: ((Float) -> Float)?,
    block: (Animatable<Float, AnimationVector1D>.() -> Unit)? = null,
): AnimationResult<Float, AnimationVector1D> {
    val targetValue = animationSpec.calculateTargetValue(value, initialVelocity)
    val adjustedTarget = adjustTarget?.invoke(targetValue)

    return if (adjustedTarget != null) {
        animateTo(
            targetValue = adjustedTarget,
            initialVelocity = initialVelocity,
            block = block
        )
    } else {
        animateDecay(
            initialVelocity = initialVelocity,
            animationSpec = animationSpec,
            block = block,
        )
    }
}