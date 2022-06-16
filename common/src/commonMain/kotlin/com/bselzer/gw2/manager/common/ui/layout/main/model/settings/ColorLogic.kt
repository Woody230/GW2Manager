package com.bselzer.gw2.manager.common.ui.layout.main.model.settings

data class ColorLogic(
    val updateInput: (String) -> Unit,
    val clearInput: () -> Unit,
    val onSave: suspend () -> Boolean,
    val onReset: suspend () -> Unit,
)