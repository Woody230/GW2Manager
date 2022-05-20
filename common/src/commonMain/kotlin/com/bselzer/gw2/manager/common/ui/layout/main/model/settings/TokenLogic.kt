package com.bselzer.gw2.manager.common.ui.layout.main.model.settings


data class TokenLogic(
    val token: () -> String?,
    val updateToken: (String?) -> Unit,
    val clearToken: () -> Unit,
    val onSave: suspend () -> Boolean,
    val onReset: suspend () -> Unit,
    val onClickHyperlink: (String) -> Boolean,
)