package com.bselzer.library.kotlin.extension.compose.ui

import androidx.annotation.StringRes
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Displays the app bar title.
 */
@Composable
fun ShowAppBarTitle(title: String) = Text(text = title, fontWeight = FontWeight.Bold)

/**
 * Displays the app bar title.
 */
@Composable
fun ShowAppBarTitle(@StringRes title: Int) = ShowAppBarTitle(stringResource(id = title))

/**
 * Displays an up-navigation icon for an app bar.
 *
 * @param onClick the on-click handler
 */
@Composable
fun ShowUpNavigationIcon(onClick: () -> Unit) = IconButton(onClick = onClick) {
    Icon(Icons.Filled.ArrowBack, contentDescription = "Up")
}

/**
 * Displays a refresh icon for an app bar.
 *
 * @param onClick the on-click handler
 */
@Composable
fun ShowRefreshIcon(onClick: suspend CoroutineScope.() -> Unit) {
    val scope = rememberCoroutineScope()
    IconButton(onClick = { scope.launch { onClick(scope) } }) {
        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
    }
}
