package com.bselzer.gw2.manager.common.ui.layout.main.model.settings

data class ZoomLogic(
    val amount: Int,
    val amountRange: IntRange,
    val onValueChange: (Int) -> Unit,
    val onSave: suspend () -> Unit,
    val onReset: suspend () -> Unit,
    val clearInput: () -> Unit,
)