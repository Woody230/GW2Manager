package com.bselzer.library.kotlin.extension.compose.unit

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

// TODO move when inline ir error is fixed
/**
 * The [Int] as [Dp].
 */
@Composable
fun Int.toDp(): Dp = LocalDensity.current.run { this@toDp.toDp() }

/**
 * The [Float] as [Dp].
 */
@Composable
fun Float.toDp(): Dp = LocalDensity.current.run { this@toDp.toDp() }

/**
 * The [Double] as [Dp].
 */
@Composable
fun Double.toDp(): Dp = toFloat().toDp()