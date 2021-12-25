package com.bselzer.gw2.manager.android.wvw.state.map.objective

import androidx.compose.ui.graphics.Color
import com.bselzer.gw2.manager.common.ui.composable.ImageState

data class ProgressionState(
    override val enabled: Boolean,
    override val link: String?,
    override val description: String = "Upgraded",
    override val width: Int,
    override val height: Int,
    override val color: Color? = null
) : ImageState