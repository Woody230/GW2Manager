package com.bselzer.gw2.manager.common.ui.layout.main.model.settings


data class TokenLogic(
    val updateInput: (String?) -> Unit,
    val clearInput: () -> Unit,
    val onSave: suspend () -> Boolean,
    val onReset: suspend () -> Unit,
    val onClickHyperlink: (String) -> Boolean,
)