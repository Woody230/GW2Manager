package com.bselzer.gw2.manager.android.wvw.state.match.pie

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.ui.composable.ImageState

data class ChartDividerState(
    override val link: String,
    override val width: Int,
    override val height: Int,
    override val enabled: Boolean = true,
    override val color: Color? = null,
    override val description: String? = null
) : ImageState