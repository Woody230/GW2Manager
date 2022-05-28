package com.bselzer.gw2.manager.common.repository.instance

import com.bselzer.gw2.manager.common.dependency.RepositoryDependencies
import com.bselzer.gw2.manager.common.repository.base.AppRepository
import com.bselzer.gw2.v2.model.world.World
import com.bselzer.ktx.kodein.db.operation.findAllOnce
import com.bselzer.ktx.kodein.db.transaction.transaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class WorldRepository(
    dependencies: RepositoryDependencies
) : AppRepository(dependencies) {
    val worlds: StateFlow<Collection<World>> = flow {
        val worlds = database.transaction().use {
            findAllOnce { clients.gw2.world.worlds() }
        }

        emit(worlds)
    }.stateIn(
        // The world is required to find the associated match so resolve it as soon as possible.
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
        scope = scope,
    )
}