package com.bselzer.gw2.manager.common.repository

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.v2.model.world.World
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.zip

class WorldRepository(dependencies: RepositoryDependencies) : AppRepository(dependencies) {
    fun worlds(): Flow<Collection<World>> = flow {
        lockedTransaction {
            with(caches.gw2.world) {
                emit(findWorlds())
            }
        }
    }

    fun selectedWorld(): Flow<World?> = preferences.wvw.selectedWorld.observeOrNull().zip(worlds()) { selectedId, worlds ->
        worlds.firstOrNull { world -> world.id == selectedId }
    }
}