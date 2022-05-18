package com.bselzer.gw2.manager.common.dependency

import com.bselzer.ktx.kodein.db.transaction.Transaction
import com.bselzer.ktx.kodein.db.transaction.transaction
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.kodein.db.Options

interface KodeinTransaction {
    val caches: Caches
    val lock: Mutex

    suspend fun <R> transaction(vararg options: Options.BatchWrite, block: suspend Transaction.() -> R): R = caches.database.transaction().use(*options, block = block)

    suspend fun <R> lockedTransaction(vararg options: Options.BatchWrite, block: suspend Transaction.() -> R): R = lock.withLock {
        transaction(*options, block = block)
    }
}