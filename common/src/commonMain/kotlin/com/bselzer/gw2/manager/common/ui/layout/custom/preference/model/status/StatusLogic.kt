package com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.status

data class StatusLogic(
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
)