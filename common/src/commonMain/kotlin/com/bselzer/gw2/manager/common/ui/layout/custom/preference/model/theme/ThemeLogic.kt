package com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.theme

data class ThemeLogic(
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
)