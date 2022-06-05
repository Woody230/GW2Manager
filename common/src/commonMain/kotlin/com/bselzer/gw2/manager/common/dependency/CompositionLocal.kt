package com.bselzer.gw2.manager.common.dependency

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.bselzer.gw2.manager.common.ui.theme.Theme

/**
 * The theme of the application.
 */
val LocalTheme = compositionLocalOf { Theme.LIGHT }

/**
 * The image cache.
 */
val LocalDependencies: ProvidableCompositionLocal<AppDependencies> = compositionLocalOf { throw NotImplementedError("Dependencies not initialized") }