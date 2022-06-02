package com.bselzer.gw2.manager.common.ui.layout.main.model.settings

import kotlin.time.DurationUnit

data class WvwIntervalLogic(
    val amount: Int,
    val unit: DurationUnit,
    val amountRange: IntRange,
    val units: List<DurationUnit>,
    val onValueChange: (Int, DurationUnit) -> Unit,
    val onSave: suspend () -> Unit,
    val onReset: suspend () -> Unit,
    val clearInput: () -> Unit
)