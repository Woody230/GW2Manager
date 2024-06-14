package com.bselzer.gw2.manager.common.repository.instance.generic

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.ktx.coroutine.sync.LockByKey
import com.bselzer.ktx.db.operation.getById
import com.bselzer.ktx.db.transaction.transaction

class ImageRepository(
    dependencies: RepositoryDependencies
) : RepositoryDependencies by dependencies {
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