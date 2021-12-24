package com.bselzer.gw2.manager.android.ui.activity.wvw.state.map.objective

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.ui.composable.ImageState

data class ClaimState(
    override val enabled: Boolean,
    override val link: String?,
    override val description: String = "Guild Claimed",
    override val width: Int,
    override val height: Int,
    override val color: Color? = null
) : ImageState