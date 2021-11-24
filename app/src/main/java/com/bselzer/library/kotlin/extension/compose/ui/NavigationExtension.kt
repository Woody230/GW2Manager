package com.bselzer.library.kotlin.extension.compose.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable


/**
 * Displays an up-navigation icon for an app bar.
 *
 * @param onClick the on-click handler
 */
@Composable
fun UpNavigationIcon(onClick: () -> Unit) = IconButton(onClick = onClick) {
    Icon(Icons.Filled.ArrowBack, contentDescription = "Up")
}