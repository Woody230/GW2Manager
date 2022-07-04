package com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.color.ColorLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.color.ColorResources
import com.bselzer.gw2.v2.model.enumeration.WvwMapType
import com.bselzer.gw2.v2.model.extension.wvw.owner
import com.bselzer.gw2.v2.resource.strings.stringDesc
import com.bselzer.ktx.compose.ui.graphics.color.Hex
import com.bselzer.ktx.compose.ui.graphics.color.colorOrNull
import com.bselzer.ktx.compose.ui.graphics.color.hex
import com.bselzer.ktx.resource.KtxResources
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format

class ColorViewModel(
    context: AppComponentContext,
    private val mapType: WvwMapType
) : ViewModel(context) {
    private val intermediary: MutableState<String?> = mutableStateOf(null)

    val resources: ColorResources
        get() = mapType.resources(intermediary.value ?: "")

    val logic: ColorLogic
        get() = mapType.logic(intermediary.value ?: "")

    private fun WvwMapType.resources(
        input: String,
    ) = ColorResources(
        image = KtxResources.images.ic_color_lens,
        title = AppResources.strings.borderlands_color.format(owner().stringDesc()),
        subtitle = owner().color().hex(),
        dialogInput = input.desc(),
        dialogSubtitle = AppResources.strings.hexadecimal_color.desc(),
        failure = AppResources.strings.color_failure.desc(),
        hasValidInput = Hex(input).colorOrNull() != null
    )

    private fun WvwMapType.logic(
        input: String,
    ) = ColorLogic(
        updateInput = { intermediary.value = it.trim() },
        clearInput = { intermediary.value = null },
        onReset = { resetPreferenceColor(owner()) },
        onSave = {
            val color = Hex(input).colorOrNull() ?: return@ColorLogic false
            setPreferenceColor(owner(), color)
            true
        },
    )
}