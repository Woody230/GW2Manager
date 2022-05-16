package com.bselzer.gw2.manager.common.ui.layout.splash.model.initialization.migration

import com.bselzer.gw2.manager.common.dependency.Dependencies
import com.bselzer.ktx.kodein.db.transaction.DBTransaction

class Migrate0To3(dependencies: Dependencies) : Migration(dependencies) {
    override val from: Int = Int.MIN_VALUE
    override val to: Int = 3
    override val reason: String = "Many model changes, including the use of value classes for ids."

    override suspend fun migrate() = with(caches.gw2) {
        DBTransaction(caches.database).use {
            clear()
        }
    }
}