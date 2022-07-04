package com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.map

data class MapLabelLogic(
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit
)