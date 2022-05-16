package com.bselzer.gw2.manager.common.repository

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.v2.model.wvw.match.WvwMatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WvwRepository(dependencies: RepositoryDependencies) : AppRepository(dependencies) {
    fun selectedMatch(): Flow<WvwMatch?> = flow {
        lockedTransaction {
            with(caches.gw2.wvw) {
                val worldId = preferences.wvw.selectedWorld.get()
                val match = if (worldId.isDefault) null else findMatch(worldId)
                emit(match)
            }
        }
    }
}