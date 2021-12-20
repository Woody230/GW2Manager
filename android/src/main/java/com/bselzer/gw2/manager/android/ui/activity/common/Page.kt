package com.bselzer.gw2.manager.android.ui.activity.common

import androidx.compose.runtime.Composable

interface Page {
    /**
     * Lays out the content.
     */
    @Composable
    fun Content()
}