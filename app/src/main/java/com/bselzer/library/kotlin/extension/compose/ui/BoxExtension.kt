package com.bselzer.library.kotlin.extension.compose.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource


/**
 * Displays the background across the entirety of the parent.
 */
// Need to use matchParentSize() so that the image does not participate in sizing and can just fill the resulting size.
@Composable
fun BoxScope.ShowBackground(@DrawableRes drawableId: Int, alignment: Alignment = Alignment.Center) =
    ShowBackground(painter = painterResource(id = drawableId), modifier = Modifier.matchParentSize(), alignment = alignment)