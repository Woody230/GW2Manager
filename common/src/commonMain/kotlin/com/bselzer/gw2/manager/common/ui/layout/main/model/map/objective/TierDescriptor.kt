package com.bselzer.gw2.manager.common.ui.layout.main.model.map.objective

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultAlpha
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class TierDescriptor(
    val link: ImageDesc?,
    val description: Flow<StringDesc>,
    val color: Color? = null,
    val alpha: Flow<Float> = flowOf(DefaultAlpha)
)