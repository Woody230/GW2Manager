package com.bselzer.gw2.manager.common.repository

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import com.bselzer.ktx.kodein.db.transaction.transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WvwRepository(dependencies: RepositoryDependencies) : RepositoryDependencies by dependencies {
    fun selectedMatch(): Flow<WvwMatch?> = flow {
        caches.database.transaction().use {
            with(caches.gw2.wvw) {
                val worldId = preferences.wvw.selectedWorld.get()
                emit(findMatch(worldId))
            }
        }
    }
}