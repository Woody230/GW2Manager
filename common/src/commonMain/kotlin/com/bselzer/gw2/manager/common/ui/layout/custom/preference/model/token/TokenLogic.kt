package com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.token


data class TokenLogic(
    val updateInput: (String?) -> Unit,
    val clearInput: () -> Unit,
    val onSave: suspend () -> Boolean,
    val onReset: suspend () -> Unit
)