package com.bselzer.gw2.manager.common.ui.layout.main.model.cache

import com.bselzer.ktx.db.transaction.Transaction

data class ClearLogic(
    val type: ClearType,
    val perform: Transaction.() -> Unit
)