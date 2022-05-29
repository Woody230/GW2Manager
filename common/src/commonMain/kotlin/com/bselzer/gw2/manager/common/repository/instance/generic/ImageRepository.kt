package com.bselzer.gw2.manager.common.repository.instance.generic

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.instance.AppRepository
import com.bselzer.ktx.compose.image.model.Image
import com.bselzer.ktx.kodein.db.operation.getById
import com.bselzer.ktx.kodein.db.transaction.transaction

class ImageRepository(
    dependencies: RepositoryDependencies
) : AppRepository(dependencies) {
    suspend fun getImage(url: String) = database.transaction().use {
        getById(
            id = url,
            requestSingle = { clients.image.getImageOrNull(url) ?: Image(url, byteArrayOf()) },
            writeFilter = { image -> image.content.isNotEmpty() }
        )
    }
}