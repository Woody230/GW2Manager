package com.bselzer.library.kotlin.extension.compose.ui

import androidx.annotation.StringRes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

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