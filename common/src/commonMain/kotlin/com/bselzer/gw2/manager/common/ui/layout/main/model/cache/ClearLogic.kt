package com.bselzer.gw2.manager.common.ui.layout.main.model.cache

import app.cash.sqldelight.TransactionWithoutReturn

data class ClearLogic(
    val type: ClearType,
    val perform: TransactionWithoutReturn.() -> Unit
)