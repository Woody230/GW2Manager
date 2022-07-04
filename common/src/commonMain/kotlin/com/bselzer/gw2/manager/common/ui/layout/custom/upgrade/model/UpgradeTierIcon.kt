package com.bselzer.gw2.manager.common.ui.layout.custom.upgrade.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.image.ImageDesc
import kotlinx.coroutines.flow.Flow

interface UpgradeTierIcon {
    val link: ImageDesc?
    val description: Flow<StringDesc>
    val alpha: Flow<Float>

    val color: Color?
        @Composable
        get
}