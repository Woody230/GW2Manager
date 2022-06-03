package com.bselzer.gw2.manager.common.repository.instance.generic

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.instance.AppRepository
import com.bselzer.ktx.coroutine.sync.LockByKey
import com.bselzer.ktx.kodein.db.operation.getById
import com.bselzer.ktx.kodein.db.transaction.transaction

class ImageRepository(
    dependencies: RepositoryDependencies
) : AppRepository(dependencies) {
    private val lock = LockByKey<String>()

    suspend fun getImage(url: String) = database.transaction().use {
        getById(
            id = url,
            requestSingle = {
                lock.withLock(url) { clients.image.imageOrDefault(url) }
            },
            writeFilter = { image -> image.content.isNotEmpty() }
        )
    }
}