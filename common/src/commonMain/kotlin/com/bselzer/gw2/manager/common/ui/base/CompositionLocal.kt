package com.bselzer.gw2.manager.common.ui.base

import androidx.compose.runtime.Composable
import com.bselzer.ktx.compose.ui.ApplicationSize

/**
 * Depending on rotation or window size, adjust whether to display horizontally or vertically.
 */
val ShouldLayoutHorizontally
    @Composable
    get() = ApplicationSize.current.width > ApplicationSize.current.height