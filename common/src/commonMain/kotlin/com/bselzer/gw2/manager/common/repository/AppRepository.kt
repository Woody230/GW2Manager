package com.bselzer.gw2.manager.common.repository

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.ktx.kodein.db.transaction.Transaction
import com.bselzer.ktx.kodein.db.transaction.transaction
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.kodein.db.Options

open class AppRepository(dependencies: RepositoryDependencies) : RepositoryDependencies by dependencies {
    private val lock = Mutex()

    protected suspend fun <R> transaction(vararg options: Options.BatchWrite, block: suspend Transaction.() -> R): R = caches.database.transaction().use(*options, block = block)

    protected suspend fun <R> lockedTransaction(vararg options: Options.BatchWrite, block: suspend Transaction.() -> R): R = lock.withLock {
        transaction(*options, block = block)
    }
}