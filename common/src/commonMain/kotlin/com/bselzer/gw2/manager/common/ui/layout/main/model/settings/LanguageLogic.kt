package com.bselzer.gw2.manager.common.ui.layout.main.model.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale

data class LanguageLogic(
    val values: List<Locale>,
    val selected: @Composable () -> Locale,
    val updateSelection: (Locale) -> Unit,
    val resetSelection: () -> Unit,
    val onSave: suspend () -> Unit,
    val onReset: suspend () -> Unit
)