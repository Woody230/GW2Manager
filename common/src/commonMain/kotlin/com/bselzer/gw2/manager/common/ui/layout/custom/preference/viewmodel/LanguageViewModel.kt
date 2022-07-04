package com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.language.LanguageLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.language.LanguageResources
import com.bselzer.ktx.compose.resource.strings.localized
import com.bselzer.ktx.intl.Locale
import com.bselzer.ktx.resource.KtxResources
import com.bselzer.ktx.resource.strings.stringResourceOrNull
import com.bselzer.ktx.settings.compose.safeState
import com.bselzer.ktx.settings.setting.Setting
import dev.icerock.moko.resources.desc.desc

class LanguageViewModel(
    context: AppComponentContext
) : ViewModel(context) {
    private val setting: Setting<Locale> = preferences.common.locale
    private val intermediary: MutableState<Locale?> = mutableStateOf(null)

    // TODO better way to manage the mixed usages of these labels in composable and non-composable contexts?
    val labels: Map<Locale, String>
        @Composable
        get() = logic.values.associateWith { locale -> resources.getLabel(locale).localized() }

    val resources: LanguageResources
        @Composable
        get() = LanguageResources(
            image = KtxResources.images.ic_language,
            title = KtxResources.strings.language.desc(),
            subtitle = (setting.safeState().value.stringResourceOrNull() ?: KtxResources.strings.locale_en).desc(),
            getLabel = { locale -> locale.stringResourceOrNull()?.desc() ?: "".desc() }
        )

    val logic: LanguageLogic
        get() = LanguageLogic(
            values = languages,
            selected = { intermediary.value ?: setting.safeState().value },
            onSave = {
                intermediary.value?.let { locale -> updateLocale(locale) }
            },
            onReset = { resetLocale() },
            updateSelection = { intermediary.value = it },
            resetSelection = { intermediary.value = null }
        )
}