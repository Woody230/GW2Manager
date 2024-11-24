package com.bselzer.gw2.manager.common.ui.layout.main.model.cache

data class ClearLogic(
    val type: ClearType,
    val perform: suspend () -> Unit
)