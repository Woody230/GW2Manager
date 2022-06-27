package com.bselzer.gw2.manager.common.configuration

import androidx.compose.ui.graphics.DefaultAlpha
import com.bselzer.gw2.manager.common.configuration.wvw.Wvw

interface Configuration {
    val wvw: Wvw
    val transparency: Float

    /**
     * @return [DefaultAlpha] if the [condition] is true, otherwise the [transparency]
     */
    fun alpha(condition: Boolean): Float = if (condition) DefaultAlpha else transparency
}