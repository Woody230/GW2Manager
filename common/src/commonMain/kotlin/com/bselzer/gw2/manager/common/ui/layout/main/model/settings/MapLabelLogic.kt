package com.bselzer.gw2.manager.common.ui.layout.main.model.settings

data class MapLabelLogic(
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
)