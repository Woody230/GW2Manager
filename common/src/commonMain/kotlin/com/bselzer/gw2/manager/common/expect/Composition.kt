package com.bselzer.gw2.manager.common.expect

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.bselzer.gw2.manager.common.state.core.Gw2State
import com.bselzer.gw2.manager.common.ui.theme.Theme

/**
 * The theme of the application.
 */
val LocalTheme = compositionLocalOf { Theme.LIGHT }

/**
 * The application state.
 */
val LocalState = staticCompositionLocalOf<Gw2State> { throw NotImplementedError("The application state is not set.") }