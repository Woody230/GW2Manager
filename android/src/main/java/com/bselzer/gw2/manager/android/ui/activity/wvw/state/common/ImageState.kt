package com.bselzer.gw2.manager.android.ui.activity.wvw.state.common

import androidx.compose.ui.graphics.Color

data class ImageState(
    val link: String?,
    val description: String?,
    val width: Int,
    val height: Int,

    /**
     * The color to transform the image into.
     */
    val color: Color
)