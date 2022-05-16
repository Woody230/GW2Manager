package com.bselzer.gw2.manager.common.repository

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.ktx.kodein.db.transaction.transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class WorldRepository(dependencies: RepositoryDependencies) : RepositoryDependencies by dependencies {
    fun worlds(): Flow<Collection<World>> = flow {
        caches.database.transaction().use {
            with(caches.gw2.world) {
                emit(findWorlds())
            }
        }
    }

    fun selectedWorld(): Flow<World?> = worlds().map { worlds ->
        worlds.firstOrNull { world ->
            world.id == preferences.wvw.selectedWorld.get()
        }
    }
}